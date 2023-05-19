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

object Test:
// Return true if match succeeds; otherwise false
// def check[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(using
//     ev: EntityDecoder[IO, A]
// ): Boolean =
//    val actualResp = actual.unsafeRunSync()
//    val statusCheck = actualResp.status == expectedStatus
//    val bodyCheck = expectedBody.fold[Boolean](
//      // Verify Response's body is empty.
//      actualResp.body.compile.toVector.unsafeRunSync().isEmpty
//    )(expected => actualResp.as[A].unsafeRunSync() == expected)
//    statusCheck && bodyCheck
end Test

class InterpretedCacheMiddleTest extends munit.CatsEffectSuite:
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
   def bytesToString(bytes: Vector[Byte]): String = bytes.map(_.toChar).mkString
   def parseMap(body: String): Map[String, Int] = body.split("\n").map(_.split(" -> "))
     .map { case Array(path, count) => (path, count.toInt) }.toMap

   val worldPeace: Uri = Uri
     .unsafeFromString("https://bblfish.net/people/henry/blog/2023/04/01/world-at-peace")
   val bbl: Uri = Uri.unsafeFromString("https://bblfish.net/")
   val counterUri = Uri.unsafeFromString("https://bblfish.net/counter")

   /** we need dates so that caching can work but it is tricky to compare them */
   def removeDate(headers: Headers): Headers =
      import org.typelevel.ci.CIStringSyntax
      headers.transform(l => l.filterNot(_.name == ci"Date"))

   test("test String Cache") {
     for
        ref <- Ref.of[IO, WebCache[CacheItem[String]]](Map.empty)
        stringCacheMiddleWare = InterpretedCacheMiddleware.app[IO, String](
          TreeDirCache[IO, CacheItem[String]](ref),
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
        cachedClient = stringCacheMiddleWare(Web.httpRoutes[IO].orNotFound)
        respWP1 <- cachedClient.run(Request[IO](Method.GET, worldPeace))
        respRoot <- cachedClient.run(Request[IO](Method.GET, bbl))
        rescounter1 <- cachedClient.run(Request[IO](Method.GET, counterUri))
        respWP2 <- cachedClient.run(Request[IO](Method.GET, worldPeace))
        rescounter2 <- cachedClient.run(Request[IO](Method.GET, counterUri))
        respRoot2 <- cachedClient.run(Request[IO](Method.GET, bbl))
        rescounter3 <- cachedClient.run(Request[IO](Method.GET, counterUri))
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

   }

end InterpretedCacheMiddleTest
