/*
 * Copyright 2021 bblfish.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
          response.headers,
          Web.headers("/")
        )
     } >> Web.httpRoutes[IO].orNotFound.run(
       Request[IO](uri =
         Uri(path = Uri.Path.unsafeFromString("/people/henry/blog/2023/04/01/world-at-peace"))
       )
     ).map { response =>
        assertEquals(response.status, Status.Ok)
        assertEquals(
          response.headers,
          Web.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(
          bytesToString(response.body.compile.toVector.unsafeRunSync()),
          "Hello World!"
        )
     }
   }
   def bytesToString(bytes: Vector[Byte]): String = bytes.map(_.toChar).mkString

   test("test string cache") {
     for ref <- Ref.of[IO, WebCache[CacheItem[String]]](Map.empty)
     yield
        val clientMiddleWare = InterpretedCacheMiddleware.app[IO, String](
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

        val bbl: Uri = Uri
          .unsafeFromString("https://bblfish.net/people/henry/blog/2023/04/01/world-at-peace")
        assertNotEquals(bbl, null)
        val interpretedClient = clientMiddleWare(Web.httpRoutes[IO].orNotFound)
        val req = Request[IO](Method.GET, bbl)
        val resp: CachedResponse[String] = interpretedClient.run(req).unsafeRunSync()
        assertEquals(resp.status, Status.Ok)
        assertEquals(
          resp.headers,
          Web.headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
        )
        assertEquals(
          resp.body,
          Some("Hello World!")
        )
     //   val resp2 = client(Web.httpRoutes[IO].orNotFound).run(req).unsafeRunSync()
     //   assertEquals(resp2.status, Status.Ok)
     //   assertEquals(
     //     resp2.headers,
     //     Web.headers("/")
     //   )
     //   assertEquals(
     //     resp2.body.compile.toVector.unsafeRunSync(),
     //     ByteVector("Hello World!".getBytes())
     //   )
   }

end InterpretedCacheMiddleTest
