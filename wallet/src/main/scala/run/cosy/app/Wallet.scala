package run.cosy.app

import org.http4s.{Request, Response}

import scala.util.Try

trait Wallet[F[_]] {
	/**
	 * if possible, sign the original request given the information provided by the 40x response
	 */
	def sign(res: Response[F], req: Request[F]): F[Try[Request[F]]]
}

