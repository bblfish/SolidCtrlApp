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

package io.chrisdavenport.mules.http4s

import cats.*
import cats.effect.*
import cats.implicits.*
import org.http4s.HttpDate

/** Cache Items are what we place in the cache, this is exposed so that caches can be constructed by
  * the user for this type
  */
final case class CacheItem[T](
    created: HttpDate,
    expires: Option[HttpDate],
    response: CachedResponse[T]
)

object CacheItem:

   def create[F[_]: Clock: MonadThrow, T](
       response: CachedResponse[T],
       expires: Option[HttpDate]
   ): F[CacheItem[T]] = HttpDate.current[F].map(date => new CacheItem(date, expires, response))

   private[http4s] final case class Age(val deltaSeconds: Long) extends AnyVal
   private[http4s] object Age:
      def of(created: HttpDate, now: HttpDate): Age = new Age(now.epochSecond - created.epochSecond)
   private[http4s] final case class CacheLifetime(val deltaSeconds: Long) extends AnyVal
   private[http4s] object CacheLifetime:
      def of(expires: Option[HttpDate], now: HttpDate): Option[CacheLifetime] = expires
        .map { expiredAt =>
          new CacheLifetime(expiredAt.epochSecond - now.epochSecond)
        }
