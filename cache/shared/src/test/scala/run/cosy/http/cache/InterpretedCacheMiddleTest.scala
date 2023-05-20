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

object InterpretedCacheMiddleTest:
   def bytesToString(bytes: Vector[Byte]): String = bytes.map(_.toChar).mkString
   def parseMap(body: String): Map[String, Int] = body.split("\n").map(_.split(" -> "))
     .map { case Array(path, count) => (path, count.toInt) }.toMap

   val worldPeace: Uri = Uri
     .unsafeFromString("https://bblfish.net/people/henry/blog/2023/04/01/world-at-peace")
   val bblBlogVirgin: Uri = Uri
     .unsafeFromString("https://bblfish.net/people/henry/blog/2023/05/18/birth")
   val bbl: Uri = Uri.unsafeFromString("https://bblfish.net/")
   val counterUri = Uri.unsafeFromString("https://bblfish.net/counter")
end InterpretedCacheMiddleTest

class InterpretedCacheMiddleTest extends munit.CatsEffectSuite:
   import InterpretedCacheMiddleTest.*
   test("test web") {
     Web.httpRoutes[IO].orNotFound.run(Request[IO](uri = Uri(path = Root))).map { response =>
        assertEquals(response.status, Status.Ok)
        assertEquals(
          removeDate(response.headers),
          Web.headers("/")
        )
     } >> Web.httpRoutes[IO].orNotFound.run(
       Request[IO](uri =
         Uri(path = Uri.Path.unsafeFromString("/people/henry/blog/2023/04/01/world-at-peace"))
       )
     ).map { response =>
        assertEquals(response.status, Status.Ok)
        assertEquals(
          removeDate(response.headers),
          Web.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
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
            }
        )
        cc = stringCacheMiddleWare(Web.httpRoutes[IO].orNotFound)
        respWP1 <- cc.run(Request[IO](GET, worldPeace))
        respRoot <- cc.run(Request[IO](GET, bbl))
        rescounter1 <- cc.run(Request[IO](GET, counterUri))
        respWP2 <- cc.run(Request[IO](GET, worldPeace))
        rescounter2 <- cc.run(Request[IO](GET, counterUri))
        respRoot2 <- cc.run(Request[IO](GET, bbl))
        rescounter3 <- cc.run(Request[IO](GET, counterUri))
        cachedWorldPeace <- cache.lookup(worldPeace)
        closestDir1 <- cache.findClosest(bblBlogVirgin)(_.isDefined)
        wpLink = cachedWorldPeace.flatMap { ci =>
           val x: List[Uri] =
             for
                links <- ci.response.headers.get[Link].toList
                link <- links.values.toList
                if link.rel.contains(Web.defaultAccessContainer)
             yield worldPeace.resolve(link.uri)
           x.headOption // there should be only one!
        }
        blogAcrDirUrl <- IO.fromOption(wpLink)(
          new Exception(s"no default container Link header in $cachedWorldPeace")
        )
        cacheBlogDir <- cc.run(Request[IO](Method.GET, blogAcrDirUrl))
        closestDir2 <- cache.findClosest(bblBlogVirgin)(_.isDefined)
     yield
        assertEquals(respWP1.status, Status.Ok)
        assertEquals(
          removeDate(respWP1.headers),
          Web.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(
          respWP1.body,
          Some("Hello World!")
        )
        assertEquals(respRoot.status, Status.Ok)
        assertEquals(
          removeDate(respRoot.headers),
          Web.headers("/")
        )
        assertEquals(
          respRoot.body,
          Some(Web.rootTtl)
        )
        assertEquals(rescounter1.status, Status.Ok)
        // this tells us that we hit the cache once
        rescounter1.body.map { body =>
           val c1 = parseMap(body)
           assertEquals(c1(worldPeace.path.toString), 1)
           assertEquals(c1(bbl.path.toString), 1)
           assertEquals(c1(counterUri.path.toString), 1)
        }.getOrElse(fail("no body"))

        assertEquals(respWP2.status, Status.Ok)
        assertEquals(
          removeDate(respWP1.headers),
          Web.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(rescounter2.status, Status.Ok)

        // this tells us that we got all the info directly from the cache
        rescounter2.body.map { body =>
           val c1 = parseMap(body)
           assertEquals(c1(worldPeace.path.toString), 1)
           assertEquals(c1(bbl.path.toString), 1)
           assertEquals(c1(counterUri.path.toString), 1)
        }.getOrElse(fail("no body for 2nd req to " + counterUri))

        assertEquals(respRoot2.status, Status.Ok)
        assertEquals(
          respRoot2.body,
          Some(Web.rootTtl)
        )
        rescounter3.body.map { body =>
           val c1 = parseMap(body)
           assertEquals(c1(worldPeace.path.toString), 1)
           assertEquals(c1(bbl.path.toString), 1)
           assertEquals(c1(counterUri.path.toString), 1)
        }.getOrElse(fail("no body for 3rd req to " + counterUri))

        // test direct access to cache
        assertEquals(cachedWorldPeace.get.response.status, Status.Ok)
        assertEquals(cachedWorldPeace.get.response.body, Some(Web.bblWorldAtPeace))

        assertEquals(cacheBlogDir.status, Status.Ok)
        assertEquals(
          removeDate(cacheBlogDir.headers),
          Web.headers("")
        )
        // the closest dir is the root because we have not cached the blog dir
        assertEquals(closestDir1.map(_.response.body), Some(Some(Web.rootTtl)))
        // now the blog dir is cached
        assertEquals(closestDir2.map(_.response.body), Some(Some(Web.bblBlogRootContainer)))
   }

end InterpretedCacheMiddleTest
