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

package net.bblfish.app.auth

import cats.effect.kernel.{Ref, Sync}
import cats.effect.std.Hotswap
import cats.effect.{Async, Concurrent, Resource}
import cats.syntax.all.*
import net.bblfish.app.Wallet
import org.http4s.Uri.Host
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect.{
  getRedirectUris,
  methodForRedirect,
  redirectUrisKey
}
import org.http4s.headers.*
import org.http4s.{BasicCredentials, Header, Request, Response, Status}

import scala.util.{Failure, Success, Try}

/** Client Authentication is a middleware that transforms a Client into a new Client that can use a
  * Wallet to have requests signed. It will try to sign a request
  *   1. before it is sent using information it has available from the local cache on the server. So
  *      it will try to find an relevant ACL that it can use to determine if it can sign something
  *   1. if the server returns a 401 it will use the response info to fetch the ACL rules and if it
  *      can, sign the request
  */
object AuthNClient:
   def apply[F[_]: Concurrent](wallet: Wallet[F])(
       client: Client[F]
   ): Client[F] =

      def authLoop(
          req: Request[F],
          attempts: Int,
          hotswap: Hotswap[F, Response[F]]
      ): F[Response[F]] = hotswap.clear *> // Release the prior connection before allocating a new
        // todo: we should enhance the req with a signature if we already have info on the server
        hotswap.swap(client.run(req)).flatMap { (resp: Response[F]) =>
          // todo: may want a lot more flexibility than attempt numbering to determine if we should retry or not.
          resp.status match
           case Status.Unauthorized if attempts < 1 =>
             wallet.sign(resp, req).flatMap(newReq => authLoop(newReq, attempts + 1, hotswap))
           case _ => resp.pure[F]
        }

      Client { req =>
        // using the pattern from FollowRedirect example using Hotswap.
        // Not 100% sure this is so much needed here...
        Hotswap.create[F, Response[F]].flatMap { hotswap =>
          Resource.eval(
            wallet.signFromDB(req).map {
              case Right(signedReq) => signedReq
              case Left(_) => req
            }.flatMap(req => authLoop(req, 0, hotswap))
          )
        }
      }
   end apply

end AuthNClient
