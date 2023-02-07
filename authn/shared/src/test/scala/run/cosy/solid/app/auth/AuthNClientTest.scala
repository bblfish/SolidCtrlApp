/*
 * Copyright 2021 Typelevel
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

package run.cosy.solid.app.auth

import cats.effect.*
import cats.effect.std.Semaphore
import cats.syntax.all.*
import io.lemonlabs
import io.lemonlabs.uri.config.UriConfig
import net.bblfish.app.auth.AuthNClient
import net.bblfish.wallet.BasicWallet
import net.bblfish.wallet.BasicId
import org.http4s.Uri.Host
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.middleware.ResponseLogger
import org.http4s.dsl.io.{->, Ok, Unauthorized, *}
import org.http4s.headers.*
import org.http4s.server.middleware.authentication.BasicAuth
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.syntax.all.*
import org.http4s.{AuthedRoutes, BasicCredentials, Challenge, Headers, HttpRoutes, Request, Response, Status, Uri}
import org.typelevel.ci.*
import org.w3.banana.jena.JenaRdf.R as Jena
import java.util.concurrent.atomic.*

case class User(id: Long, name: String)

class AuthNClientTest extends munit.CatsEffectSuite {

  val realm = "Test Realm"
  val username = "Test User"
  val password = "Test Password"

  def publicRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root =>
    Ok("Hello World")
  }

  val authedRoutes: AuthedRoutes[String, IO] = AuthedRoutes.of[String, IO] {
    case GET -> Root as user => Ok(user)
    case req as _            => Response.notFoundFor(req)
  }

  def validatePassword(creds: BasicCredentials): IO[Option[String]] =
    IO.pure {
      if (creds.username == username && creds.password == password)
        Some(creds.username)
      else None
    }

  val basicAuthMiddleware: AuthMiddleware[IO, String] =
    BasicAuth(realm, validatePassword)

  //
  // test server
  //
  {
    val basicAuthedService = basicAuthMiddleware(authedRoutes)

    def routes: HttpRoutes[IO] = Router[IO](
      "/pub" -> publicRoutes,
      "/auth" -> basicAuthedService
    )

    test("public Route needs no authentication") {
      routes(Request[IO](uri = uri"/pub/")).map { (res: Response[IO]) =>
        assertEquals(res.status, Status.Ok)
      }
    }

    test(
      "BasicAuthentication should respond to a request with unknown username with 401"
    ) {
      val req = Request[IO](
        uri = uri"/auth",
        headers = Headers(Authorization(BasicCredentials("Wrong User", password)))
      )
      routes(req).foldF(IO(fail("no route"))) { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Unauthorized)) >>
          IO(
            assertEquals(
              res.headers.get[`WWW-Authenticate`].map(_.value),
              Some(
                Challenge("Basic", realm, Map("charset" -> "UTF-8")).toString
              )
            )
          )
      }
    }

    test(
      "BasicAuthentication should fail to respond to a request for non existent resource"
    ) {
      val req = Request[IO](
        uri = uri"/doesNotExist",
        headers = Headers(Authorization(BasicCredentials("Wrong User", password)))
      )
      routes(req).foldF(IO(assert(true, "route does not exist"))) { (res: Response[IO]) =>
        IO(fail("route does not exist"))
      }
    }

    test(
      "BasicAuthentication should respond to a request with correct credentials"
    ) {
      val req = Request[IO](
        uri = uri"/auth",
        headers = Headers(Authorization(BasicCredentials(username, password)))
      )
      routes(req).foldF(IO(fail("no route"))) { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Ok)) >>
          res.as[String].map(s => assertEquals(s, username))
      }
    }

    test(
      "BasicAuthentication responds to authenticated non-existent resource with 404"
    ) {
      val req = Request[IO](
        uri = uri"/auth/nonExistent",
        headers = Headers(Authorization(BasicCredentials(username, password)))
      )
      routes(req).foldF(IO(fail("no route"))) { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.NotFound))
      }
    }

    // test with client now
    val defaultClient: Client[IO] = Client.fromHttpApp(routes.orNotFound)
    given UriConfig = UriConfig.default
    //		val logedClient: Client[IO] = ResponseLogger[IO](true, true, logAction = Some(s => IO(println(s))))(defaultClient)
    val client: Client[IO] = AuthNClient[IO](
      new BasicWallet[IO,Jena](
        Map(
          lemonlabs.uri.Authority("localhost") ->
            BasicId(username, password)
        )
      )
    )(defaultClient)

    val clientBad: Client[IO] = AuthNClient[IO](
      BasicWallet[IO,Jena](
        Map(
          lemonlabs.uri.Authority("localhost") -> BasicId(
            username,
            password + "bad"
          )
        ),
        List()
      )
    )(defaultClient)

    test("Wallet Based Auth") {
      client.get(uri"http://localhost/auth") { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Ok)) >>
          res.as[String].map(s => assertEquals(s, username))

      }
    }

    test("Wallet Based Auth on Non Existent resource") {
      client.get(uri"http://localhost/auth") { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Ok)) >>
          res.as[String].map(s => assertEquals(s, username))

      }
    }

    test("Wallet Based Auth with bad password fails on protected resources") {
      clientBad.get(uri"http://localhost/auth") { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Unauthorized))
      }
      clientBad.get(uri"http://localhost/auth/NonExistent") { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Unauthorized))
      }
    }

    test("Wallet Based Auth with bad password succeeds on public resources") {
      clientBad.get(uri"http://localhost/pub") { (res: Response[IO]) =>
        IO(assertEquals(res.status, Status.Ok)) >>
          res.as[String].map(s => assertEquals(s, "Hello World"))
      }
    }
  }

}
