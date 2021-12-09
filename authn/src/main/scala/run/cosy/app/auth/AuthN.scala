package run.cosy.app.auth

import cats.effect.kernel.Sync
import cats.effect.kernel.Ref
import cats.effect.std.Hotswap
import cats.effect.{Async, Concurrent, Resource}
import org.http4s.{BasicCredentials, Header, Request, Response, Status}
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect.{getRedirectUris, methodForRedirect, redirectUrisKey}
import org.http4s.headers.Location
import cats.syntax.all.*
import org.http4s.Uri.Host
import org.http4s.headers.Authorization
import org.http4s.headers.*

import scala.util.{Failure, Success, Try}
import scala.util.Try

trait Wallet[F[_]] {
	/**
	 * if possible, sign the request given the information provided by the 40x response
	 */
	def sign(res: Response[F], req: Request[F]): F[Try[Request[F]]]
}

object AuthN {
	def apply[F[_]: Concurrent](wallet: Wallet[F])(
		client: Client[F]
	): Client[F] =
		def authLoop(
			req: Request[F],
			attempts: Int,
			hotswap: Hotswap[F, Response[F]]
		): F[Response[F]] =
			hotswap.swap(client.run(req)).flatMap { (resp: Response[F]) =>
				//todo: may want a lot more flexibility than attempt numbering to determine if we should retry or not.
				resp.status match
				case Status.Unauthorized if attempts < 1 =>
					wallet.sign(resp, req).flatMap {
						case Success(newReq) =>
							authLoop(newReq, attempts + 1, hotswap)
						case Failure(e) =>
							//todo: add an attribute to explain failure
							resp.pure[F]
					}
				case _ => resp.pure[F]
			}

		Client{ req =>
			//using the pattern from FollowRedirect example using Hotswap.
			//Not 100% sure this is so much needed here...
			Hotswap.create[F, Response[F]].flatMap { hotswap =>
				Resource.eval(authLoop(req, 0, hotswap))
			}
		}
	end apply

	def basicWallet[F[_]: Concurrent](
		db: Map[Host,BasicId]
	): Wallet[F] = new BasicWallet[F](db)

	class BasicId(val username: String, val password: String)

	// very non modifiable wallet.
	class BasicWallet[F[_]: Concurrent](
		db: Map[Host,BasicId]
	) extends Wallet[F] {
		override def sign(res: Response[F], req: Request[F]): F[Try[Request[F]]] =
			// a realistic implementation would be much more complex
			// 1. it would potentially have to make requests over the web (eg. fetch access control rule)
			// 2. it would look at the response to see what headers say to know what signature to provide
			summon[Concurrent[F]].pure(
				req.uri.authority.toRight(new Error("no authority")).toTry
					.flatMap(auth =>
						db.get(auth.host).toRight(new Error("No password for " + auth.host)).toTry
							.map { bid =>
								val a: Authorization = Authorization(BasicCredentials(bid.username, bid.password))
								req.putHeaders(a)
							}
					)
			)
	}



}
