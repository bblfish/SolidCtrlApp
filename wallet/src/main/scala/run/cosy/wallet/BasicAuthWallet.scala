package run.cosy.wallet

import cats.effect.Concurrent
import org.http4s.Uri.Host
import org.http4s.headers.Authorization
import org.http4s.{BasicCredentials, Request, Response}
import run.cosy.app.Wallet

import scala.util.Try

object BasicAuthWallet {
	def basicWallet[F[_] : Concurrent](
		db: Map[Host, BasicId]
	): Wallet[F] = new BasicWallet[F](db)

	class BasicId(val username: String, val password: String)

	// very non modifiable wallet.
	class BasicWallet[F[_] : Concurrent](
		db: Map[Host, BasicId]
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
