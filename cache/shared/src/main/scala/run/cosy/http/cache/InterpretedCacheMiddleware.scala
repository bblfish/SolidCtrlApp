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

package run.cosy.http.cache

import cats.data.Kleisli
import cats.effect.{Clock, Concurrent, Resource}
import io.chrisdavenport.mules.http4s.internal.Caching
import io.chrisdavenport.mules.http4s.{CacheItem, CacheType, CachedResponse}
import io.chrisdavenport.mules.{Cache, DeleteBelow}
import org.http4s.*
import org.http4s.client.Client
import cats.arrow.FunctionK
import run.cosy.http.cache.TreeDirCache

/** Interpreted Cache don't just cache the resource but the interpretation of that resource, e.g.
  * the parsed JSON or XML, or an RDF Graph or Quads
  */
object InterpretedCacheMiddleware:
   type InterpClient[F[_], G[_], T] = Kleisli[G, Request[F], CachedResponse[T]]
   type TDCache[F[_], K, V] = Cache[F, K, V] with DeleteBelow[F, K]

   def client[F[_]: Concurrent: Clock, T](
       cache: TDCache[F, Uri, CacheItem[T]],
       interpret: Response[F] => F[CachedResponse[T]],
       enhance: Request[F] => Request[F] = identity,
       cacheType: CacheType = CacheType.Private
   ): Client[F] => InterpClient[F, Resource[F, *], T] = (client: Client[F]) =>
     Kleisli(
       Caching[F, T](cache, interpret, cacheType)
         .request(Kleisli(req => client.run(enhance(req))), Resource.liftK)
     )

   def app[F[_]: Concurrent: Clock, T](
       cache: TreeDirCache[F, CacheItem[T]],
       interpret: Response[F] => F[CachedResponse[T]],
       enhance: Request[F] => Request[F] = identity,
       cacheType: CacheType = CacheType.Private
   ): HttpApp[F] => InterpClient[F, F, T] = (app: HttpApp[F]) =>
     Kleisli(
       Caching[F, T](cache, interpret, cacheType)
         .request(Kleisli(req => app.run(enhance(req))), cats.arrow.FunctionK.id[F])
     )
