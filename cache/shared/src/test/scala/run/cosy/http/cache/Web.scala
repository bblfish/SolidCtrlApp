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
import org.http4s.CharsetRange.Atom
import java.util.concurrent.atomic.AtomicReference
import cats.data.Kleisli
import cats.FlatMap
import cats.data.OptionT
import cats.Monad
import cats.effect.kernel.Clock
// import io.chrisdavenport.mules.http4s.CachedResponse.body

object Web:

   extension (uri: Uri)
     /** Uri("/") + ".acl" == Uri("/.acl") and Uri("foo")+".acl" == Uri("foo.acl") */
     def +(ext: String): Uri =
       if uri.path.endsWithSlash then uri.copy(path = uri.path / ext)
       else
          uri.copy(path =
            Uri.Path(uri.path.segments.dropRight(1)) / (uri.path.segments.last.toString + ext)
          )

   def headers(
       doc: String,
       default: Option[String] = None,
       mime: MediaType = MediaType("text", "turtle")
   ): Headers =
      val docAcrUri = Uri.unsafeFromString(doc + ".acr")
      val defaultUri: Option[Uri] = default.map(Uri.unsafeFromString)
      Headers(
        `Content-Type`(mime),
        Link(
          LinkValue(docAcrUri, rel = Some("acl")),
          defaultUri.map(ac => LinkValue(ac, rel = Some("defaultAccessContainer"))).toSeq*
        ),
        Allow(GET, HEAD)
      )

   val rootDir = Uri(path = Root)
   // relative URLs
   val thisDoc = Uri.unsafeFromString("")
   val thisDir = Uri.unsafeFromString(".")
   val rootAcr = """
     |@prefix wac: <http://www.w3.org/ns/auth/acl#> .
     |@prefix foaf: <http://xmlns.com/foaf/0.1/> .
     |
     |<#R1> a wac:Authorization;
     |   wac:mode wac:Control;
     |   wac:agent </owner#>;
     |   wac:default <.> .
     |
     |<#R2> a wac:Authorization;
     |   wac:mode wac:Read;
     |   wac:agentClass foaf:Agent;
     |   wac:accessTo <.> ;
     |   wac:default <.> .
     | """.stripMargin

   val rootTtl = """
     |@prefix ldp: <http://www.w3.org/ns/ldp#> .
     |
     |<> a ldp:BasicContainer;
     |  ldp:contains <people> .
     |""".stripMargin

   val bblCardTtl = """
     |<#i> a foaf:Person;
     |
     |	foaf.name "Henry Story";
     |	foaf.knows <https://www.w3.org/People/Berners-Lee/card#i> .
     |""".stripMargin

   val bblBlogAcr = """
     |@prefix wac: <http://www.w3.org/ns/auth/acl#> .
     |@prefix foaf: <http://xmlns.com/foaf/0.1/> .
     |
     |<#R1> a wac:Authorization;
     |   wac:mode wac:Control;
     |   wac:agent <card#me>;
     |   wac:default <.> .
     |
     |<#R2> a wac:Authorization;
     |   wac:mode wac:Read;
     |   wac:agentClass foaf:Agent;
     |   wac:default <.> .
     |
     |<#R3> a wac:Authorization;
     |   wac:mode wac:Read;
     |   wac:agentClass foaf:Agent;
     |   wac:default <.> .
     |""".stripMargin

   def httpRoutes[F[_]: Monad: Clock](using
       AS: Async[F]
   ): HttpRoutes[F] =
      val counter = AtomicReference(Map.empty[Uri.Path, Int])
      val inc = Kleisli[OptionT[F, *], Request[F], Request[F]] { req =>
         OptionT.liftF(AS.delay {
           counter.updateAndGet { m =>
              val count = m.getOrElse(req.uri.path, 0)
              m.updated(req.uri.path, count + 1)
           }
         }) >> OptionT.pure(req)
      }
      val routes: Kleisli[OptionT[F, *], Request[F], Response[F]] = HttpRoutes.of[F] {
        case GET -> Root => AS.pure(
            Response[F](
              status = Status.Ok,
              entity = Entity.strict(ByteVector(rootTtl.getBytes())),
              headers = headers("/")
            )
          )
        case GET -> Root / ".acr" => AS.pure(
            Response[F](
              status = Status.Ok,
              entity = Entity.strict(ByteVector(rootAcr.getBytes())),
              headers = headers("/")
            )
          )
        case GET -> Root / "people" / "henry" / "card" => AS.pure(
            Response(
              status = Status.Ok,
              entity = Entity.strict(ByteVector(bblCardTtl.getBytes())),
              headers = headers("card", Some("/"))
            )
          )
        case GET -> Root / "people" / "henry" / "blog" / "2023" / "04" / "01" / "world-at-peace" =>
          OK[F](
            "Hello World!",
            headers("world-at-peace", Some("/people/henry/blog/"), MediaType.text.plain)
          )
        case GET -> Root / "people" / "henry" / "blog" / ".acr" => OK[F](
            bblBlogAcr,
            headers("")
          )
        case GET -> Root / "counter" =>
          val cntStr = counter.get().toList.map { (p, i) => p.toString + " -> " + i }.mkString("\n")
          OK[F](cntStr, headers("/counter", Some("/"), MediaType.text.plain))
      }
      val addTime: Kleisli[OptionT[F, *], Response[F], Response[F]] =
        Kleisli[OptionT[F, *], Response[F], Response[F]] { resp =>
          OptionT.liftF[F, HttpDate](HttpDate.current[F]).map { now =>
            resp.putHeaders(Date(now))
          }
        }
      inc andThen routes andThen addTime

   def OK[F[_]: Async](body: String, headers: Headers): F[Response[F]] = Async[F].pure(
     Response[F](
       status = Status.Ok,
       entity = Entity.strict(ByteVector(body.getBytes())),
       headers = headers
     )
   )
