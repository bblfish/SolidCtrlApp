package run.cosy.http.cache

import cats.effect.{IO, Async}
import cats.syntax.all.*
import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*
import org.http4s.Uri.Path.Root
import org.http4s.Method.{GET, HEAD}
import scodec.bits.ByteVector
import org.http4s.dsl.io.*
import run.cosy.http.cache.TreeDirCache.WebCache
import cats.effect.kernel.Ref
import cats.data.Kleisli
import io.chrisdavenport.mules.http4s.CachedResponse
import io.chrisdavenport.mules.http4s.CacheItem
import org.typelevel.ci.CIStringSyntax
import cats.data.NonEmptyList
import io.chrisdavenport.mules.http4s.internal.Caching

object InterpretedCacheMiddleTest:
   def bytesToString(bytes: Vector[Byte]): String = bytes.map(_.toChar).mkString
   def parseMap(body: String): Map[String, Int] = body.split("\n").map(_.split(" -> "))
     .map { case Array(path, count) => (path, count.toInt) }.toMap

   val worldPeace: Uri = Uri
     .unsafeFromString("https://bblfish.net/people/henry/blog/2023/04/01/world-at-peace")
   val bblBlogVirgin: Uri = Uri
     .unsafeFromString("https://bblfish.net/people/henry/blog/2023/05/18/birth")
   val bbl: Uri = Uri.unsafeFromString("https://bblfish.net/")
   val bblBlogDir: Uri = Uri.unsafeFromString("https://bblfish.net/people/henry/blog/")
   val counterUri = Uri.unsafeFromString("https://bblfish.net/counter")
   val counterReq =
     Request[IO](GET, counterUri, headers = Headers(`Cache-Control`(CacheDirective.`no-cache`())))

   case class CacheInfo(
       worldPeace: Int,
       bbl: Int,
       blogDir: Int,
       counter: Int
   )

   def parseBody(body: String): CacheInfo =
      val c1 = parseMap(body)
      CacheInfo(
        c1.getOrElse(worldPeace.path.toString, 0),
        c1.getOrElse(bbl.path.toString, 0),
        c1.getOrElse(bblBlogDir.path.toString, 0),
        c1.getOrElse(counterUri.path.toString, 0)
      )

end InterpretedCacheMiddleTest

class InterpretedCacheMiddleTest extends munit.CatsEffectSuite:
   import InterpretedCacheMiddleTest.*
   test("test web") {
     WebTest.httpRoutes[IO].orNotFound.run(Request[IO](uri = Uri(path = Root))).map { response =>
        assertEquals(response.status, Status.Ok)
        assertEquals(
          removeDate(response.headers),
          WebTest.headers("/")
        )
     } >> WebTest.httpRoutes[IO].orNotFound.run(
       Request[IO](uri =
         Uri(path = Uri.Path.unsafeFromString("/people/henry/blog/2023/04/01/world-at-peace"))
       )
     ).map { response =>
        assertEquals(response.status, Status.Ok)
        assertEquals(
          removeDate(response.headers),
          WebTest.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(
          bytesToString(response.body.compile.toVector.unsafeRunSync()),
          "Hello World!"
        )
     }
   }

   /** we need dates so that caching can work but it is tricky to compare them */
   def removeDate(headers: Headers): Headers = headers
     .transform(l => l.filterNot(_.name == ci"Date"))

   test("test String Cache") {
     for
        ref <- Ref.of[IO, WebCache[CacheItem[String]]](Map.empty)
        cache = TreeDirCache[IO, CacheItem[String]](ref)
        stringCacheMiddleWare = InterpretedCacheMiddleware.app[IO, String](
          cache,
          (response: Response[IO]) =>
            response.bodyText.compile.toVector.map { vec =>
              CachedResponse(
                response.status,
                response.httpVersion,
                response.headers,
                Some(vec.mkString)
              )
            },
          enhance = _.putHeaders(Accept(MediaType.text.plain))
        )
        cc = stringCacheMiddleWare(WebTest.httpRoutes[IO].orNotFound)
        rescounter0 <- cc.run(counterReq)
        respWP1 <- cc.run(Request[IO](GET, worldPeace))
        respRoot <- cc.run(Request[IO](GET, bbl))
        respBlog1 <- cc.run(Request[IO](HEAD, bblBlogDir))
        rescounter1 <- cc.run(counterReq)
        respWP2 <- cc.run(Request[IO](GET, worldPeace))
        respBlog2 <- cc.run(Request[IO](GET, bblBlogDir))
        rescounter2 <- cc.run(counterReq)
        respRoot2 <- cc.run(Request[IO](GET, bbl))
        respBlog3 <- cc.run(Request[IO](HEAD, bblBlogDir))
        rescounter3 <- cc.run(counterReq)
        cachedWorldPeace <- cache.lookup(worldPeace)
        closestDir1 <- cache.findClosest(bblBlogVirgin)(_.isDefined)
        wpAcrLink = cachedWorldPeace.flatMap { ci =>
           val x: List[Uri] =
             for
                links <- ci.response.headers.get[Link].toList
                link <- links.values.toList
                if link.rel.contains(WebTest.defaultAccessContainer)
             yield worldPeace.resolve(link.uri)
           x.headOption // there should be only one!
        }
        blogDirUrl <- IO.fromOption(wpAcrLink)(
          new Exception(s"no default container Link header in $cachedWorldPeace")
        )
        cacheBlogDir <- cc.run(Request[IO](Method.GET, blogDirUrl))
        blogDirAcr = cacheBlogDir.headers.get[Link].toList.flatMap(_.values.toList)
          .collectFirst { case LinkValue(acl, Some("acl"), _, _, _) => acl }
          .map(u => blogDirUrl.resolve(u))
        x = assert(blogDirAcr.nonEmpty, s"no acl link header in $cacheBlogDir")
        closestDir2 <- cache.findClosest(bblBlogVirgin)(_.isDefined)
        cachedBlogDirAcl <- cc.run(Request[IO](Method.GET, blogDirAcr.get))
        cachedBlogDir2 <- cache.lookup(blogDirUrl)
        respBlog4 <- cc.run(
          Request[IO](
            GET,
            bblBlogDir,
            headers = Headers(Authorization(Credentials.Token(AuthScheme.Bearer, "abc")))
          )
        )
        rescounter4 <- cc.run(counterReq)
     yield
        // this tells us that we start with the server having all counters at 0
        rescounter0.body.map { body =>
          assertEquals(parseBody(body), CacheInfo(0, 0, 0, 1), body)
        }.getOrElse(fail("no body for 2nd req to " + counterUri))

        assertEquals(respWP1.status, Status.Ok)
        assertEquals(
          removeDate(respWP1.headers),
          WebTest.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(
          respWP1.body,
          Some("Hello World!")
        )
        assertEquals(respRoot.status, Status.Ok)
        assertEquals(
          removeDate(respRoot.headers),
          WebTest.headers("/")
        )
        assertEquals(
          respRoot.body,
          Some(WebTest.rootTtl)
        )
        assertEquals(respBlog1.status, Status.Unauthorized)
        assertEquals(
          removeDate(respBlog1.headers),
          WebTest.bblBlogRootHeader
        )
        assertEquals(
          respBlog1.body,
          Some(""),
          "the body on the access controlled resource is empty, as the result is unauthorized"
        )
        assertEquals(rescounter1.status, Status.Ok)
        // this tells us that we hit the cache once
        rescounter1.body.map { body =>
          assertEquals(parseBody(body), CacheInfo(1, 1, 1, 2), body)
        }.getOrElse(fail("no body"))

        assertEquals(respWP2.status, Status.Ok)
        assertEquals(
          removeDate(respWP1.headers),
          WebTest.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )

        assertEquals(respBlog2.status, Status.Unauthorized)
        assertEquals(
          removeDate(respBlog2.headers),
          WebTest.bblBlogRootHeader
        )
        assertEquals(
          respBlog2.body,
          Some(""),
          "the body on the access controlled resource is empty, as the result is unauthorized"
        )

        // a HEAD after a GET should also get info directly from the cache
        assertEquals(rescounter2.status, Status.Ok)
        rescounter2.body.map { body =>
          assertEquals(
            parseBody(body),
            CacheInfo(1, 1, 2, 3),
            "A GET after a head on the blogDir requires a new request to the server, though arguably\n" +
              " after a 401 the result should be the same unless and Authorization header is present."
          )
        }.getOrElse(fail("no body for 2nd req to " + counterUri))

        assertEquals(respRoot2.status, Status.Ok)
        assertEquals(
          respRoot2.body,
          Some(WebTest.rootTtl)
        )
        assertEquals(respBlog3.status, Status.Unauthorized)
        assertEquals(
          removeDate(respBlog3.headers),
          WebTest.bblBlogRootHeader
        )
        assertEquals(
          respBlog3.body,
          None,
          "A HEAD Request strips the body"
        )
        // this tells us that we got all the info directly from the cache
        rescounter3.body.map { body =>
          assertEquals(parseBody(body), CacheInfo(1, 1, 2, 4), body)
        }.getOrElse(fail("no body for 3rd req to " + counterUri))

        // test direct access to cache
        assertEquals(cachedWorldPeace.get.response.status, Status.Ok)
        assertEquals(cachedWorldPeace.get.response.body, Some(WebTest.bblWorldAtPeace))

        assertEquals(cacheBlogDir.status, Status.Unauthorized)
        assertEquals(
          removeDate(cacheBlogDir.headers),
          WebTest.bblBlogRootHeader
        )
        // the closest dir to the virgin blog is the bblBlogDir because we cached it above
        assertEquals(closestDir1.map(_.response.status), Some(Status.Unauthorized))
        assertEquals(closestDir1.map(_.response.body), Some(Some("")))
        // now the blog dir is cached
        assertEquals(
          closestDir2.map(_.response.body),
          Some(Some("")),
          "the body is empty because it is a 401"
        )
        // we got the acl for the blog dir after following the link header of a 401 resource
        assertEquals(cachedBlogDirAcl.status, Status.Ok)
        assertEquals(cachedBlogDirAcl.body, Some(WebTest.bblBlogAcr))

        // the cache should have the same content as the response, even when returning a 401
        // (ie, we did not have to make a new request to the web)
        assertEquals(cachedBlogDir2.get.response.status, Status.Unauthorized)
        assertEquals(
          removeDate(cachedBlogDir2.get.response.headers),
          WebTest.bblBlogRootHeader
        )

        // now we make a get request with a bearer token, giving us a body
        assertEquals(respBlog4.status, Status.Ok)
        assertEquals(
          respBlog4.body,
          Some(WebTest.bblBlogRootContainer)
        )
        // so that bearer token request made a new connection to the server
        rescounter4.body.map { body =>
          assertEquals(parseBody(body), CacheInfo(1, 1, 3, 5), body)
        }.getOrElse(fail("no body for 4rth req to " + counterUri))

   }

end InterpretedCacheMiddleTest
