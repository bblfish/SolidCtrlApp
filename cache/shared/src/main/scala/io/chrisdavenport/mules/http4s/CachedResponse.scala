/** Copyright 2019 Christopher Davenport
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
  * and associated documentation files (the "Software"), to deal in the Software without
  * restriction, including without limitation the rights to use, copy, modify, merge, publish,
  * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
  * Software is furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all copies or
  * substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
  * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
  * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
   def map[S](f: T => S): CachedResponse[S] = new CachedResponse[S](
     this.status,
     this.httpVersion,
     this.headers,
     this.body.map(f)
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
