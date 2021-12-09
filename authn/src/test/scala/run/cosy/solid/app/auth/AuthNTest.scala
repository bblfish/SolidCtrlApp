package run.cosy.solid.app.auth

import cats.effect.*
import cats.effect.std.Semaphore
import cats.syntax.all.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io.{->, Ok, Unauthorized, *}
import org.http4s.headers.*
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.authentication.BasicAuth
import org.http4s.syntax.all.*
import org.http4s.{AuthedRoutes, BasicCredentials, Challenge, Headers, Request, Response, Status, Uri}
import org.typelevel.ci.*
import run.cosy.app.auth.AuthN
import org.http4s.Uri.Host
import org.http4s.client.middleware.ResponseLogger

import java.util.concurrent.atomic.*

case class User(id: Long, name: String)

class AuthNTest extends munit.CatsEffectSuite {

	val realm = "Test Realm"
	val username = "Test User"
	val password = "Test Password"

	val service: AuthedRoutes[String, IO] = AuthedRoutes.of[String, IO] {
		case GET -> Root as user => Ok(user)
		case req as _ => Response.notFoundFor(req)
	}

	def validatePassword(creds: BasicCredentials): IO[Option[String]] =
		IO.pure {
			if (creds.username == username && creds.password == password) Some(creds.username)
			else None
		}

	val basicAuthMiddleware: AuthMiddleware[IO, String] = BasicAuth(realm, validatePassword)

   //
	// test server
	//
	{
		val basicAuthedService = basicAuthMiddleware(service)

		test("BasicAuthentication should respond to a request with unknown username with 401") {
			val req = Request[IO](
				uri = uri"/",
				headers = Headers(Authorization(BasicCredentials("Wrong User", password)))
			)
			basicAuthedService.orNotFound(req).map { (res: Response[IO]) =>
				assertEquals(res.status, Unauthorized)
				assertEquals(
					res.headers.get[`WWW-Authenticate`].map(_.value),
					Some(Challenge("Basic", realm, Map("charset" -> "UTF-8")).toString)
				)
			}
		}

		test("BasicAuthentication should respond to a request with correct credentials") {
			val req = Request[IO](
				uri = uri"/",
				headers = Headers(Authorization(BasicCredentials(username, password)))
			)
			basicAuthedService
				.orNotFound(req)
				.map(_.status)
				.assertEquals(Ok)
		}

		// test with client now
		val defaultClient: Client[IO] = Client.fromHttpApp(basicAuthedService.orNotFound)
//		val logedClient: Client[IO] = ResponseLogger[IO](true, true, logAction = Some(s => IO(println(s))))(defaultClient)
		val client: Client[IO] = AuthN[IO](AuthN.basicWallet(
			Map(Uri.RegName("localhost") -> new AuthN.BasicId(username,password))
		))(defaultClient)

		test("basic authentication with client") {
			client.get(uri"http://localhost/"){ (res: Response[IO]) =>
				IO(assertEquals(res.status, Status.Ok)) >>
					res.as[String].map(s => assertEquals(s, username))

			}
		}

		test("no authentication on non-existent resource") {
			client.get(uri"http://localhost/NotFound"){ (res: Response[IO]) =>
				IO(assertEquals(res.status, Status.NotFound))
			}
		}

	}




}