package run.cosy.app.auth

import cats.effect.kernel.{Ref, Sync}
import cats.effect.std.Hotswap
import cats.effect.{Async, Concurrent, Resource}
import cats.syntax.all.*
import org.http4s.Uri.Host
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect.{getRedirectUris, methodForRedirect, redirectUrisKey}
import org.http4s.headers.*
import org.http4s.{BasicCredentials, Header, Request, Response, Status}
import run.cosy.app.Wallet

import scala.util.{Failure, Success, Try}

/**
 * Client Authentication is a middleware that transforms a Client into a new Client that
 * can use a Wallet to have requests signed.
 */
object AuthNClient:
	def apply[F[_] : Concurrent](wallet: Wallet[F])(
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

		Client { req =>
			//using the pattern from FollowRedirect example using Hotswap.
			//Not 100% sure this is so much needed here...
			Hotswap.create[F, Response[F]].flatMap { hotswap =>
				Resource.eval(authLoop(req, 0, hotswap))
			}
		}
	end apply

end AuthNClient

