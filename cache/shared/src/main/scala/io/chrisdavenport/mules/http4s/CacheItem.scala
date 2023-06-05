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
import cats.effect.*
import cats.implicits.*
import org.http4s.HttpDate
import org.http4s.Method

/** Cache Items are what we place in the cache, this is exposed so that caches can be constructed by
  * the user for this type
  */
final case class CacheItem[T](
    requestMethod: Method,
    created: HttpDate,
    expires: Option[HttpDate],
    response: CachedResponse[T]
)

object CacheItem:

   def create[F[_]: Clock: MonadThrow, T](
       requestMethod: Method,
       response: CachedResponse[T],
       expires: Option[HttpDate]
   ): F[CacheItem[T]] = HttpDate.current[F]
     .map(date => new CacheItem(requestMethod, date, expires, response))

   private[http4s] final case class Age(val deltaSeconds: Long) extends AnyVal
   private[http4s] object Age:
      def of(created: HttpDate, now: HttpDate): Age = new Age(now.epochSecond - created.epochSecond)
   private[http4s] final case class CacheLifetime(val deltaSeconds: Long) extends AnyVal
   private[http4s] object CacheLifetime:
      def of(expires: Option[HttpDate], now: HttpDate): Option[CacheLifetime] = expires
        .map { expiredAt =>
          new CacheLifetime(expiredAt.epochSecond - now.epochSecond)
        }
