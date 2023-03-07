package net.bblfish.wallet

import cats.Id
import org.http4s.Request
import org.http4s.headers.Authorization

/**
  * The signature for authenticating a Request.
  *
  * @tparam F The monad for the Stream
  * @tparam S ASync monad for time it takes to get signature - could be Id monad on many platforms
  */
trait AuthN[F[_], S[_]]:
  /** Signing the request
    * This could be a function, but on some platforms and for some signing algorithms
    * the signing may need to be asynchronous in S[_]. The internal monad F on the other
    * had will be asyncrhonous, as it is needed for streaming responses.
    */
  def sign(originalReq: Request[F]): S[Request[F]]
  
  
class Basic[F[_]](username: String, pass: String) extends AuthN[F, Id]:
  override
  def sign(originalReq: Request[F]): Id[Request[F]] =
    originalReq.withHeaders(
      Authorization(org.http4s.BasicCredentials(username, pass))
    )
