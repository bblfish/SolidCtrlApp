/*
 * Copyright 2019 Chris Davenport
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

package io.chrisdavenport.mules.http4s

import cats.*
import cats.implicits.*
import fs2.*
import org.http4s.*
import org.typelevel.vault.Vault
import scodec.bits.ByteVector

// As attributes can be unbound. We cannot cache them as they may not be safe to do so.
final case class CachedResponse[T](
    status: Status,
    httpVersion: HttpVersion,
    headers: Headers,
    body: Option[T]
):
   def withHeaders(headers: Headers): CachedResponse[T] = new CachedResponse[T](
     this.status,
     this.httpVersion,
     headers,
     this.body
   )

object CachedResponse:

   def errorResponse[T](status: Status): CachedResponse[T] = new CachedResponse[T](
     status,
     HttpVersion.`HTTP/1.1`,
     Headers.empty,
     None
   )

   def fromResponse[F[_], G[_]: Functor](
       response: Response[F]
   )(using compiler: Compiler[F, G]): G[CachedResponse[ByteVector]] = response.body.compile
     .to(ByteVector).map { bv =>
       new CachedResponse[ByteVector](
         response.status,
         response.httpVersion,
         response.headers,
         Some(bv)
       )
     }

end CachedResponse
