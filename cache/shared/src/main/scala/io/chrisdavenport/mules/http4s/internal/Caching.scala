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

package io.chrisdavenport.mules.http4s.internal

import cats.*
import cats.data.*
import cats.effect.*
import cats.syntax.all.*
import io.chrisdavenport.mules.*
import io.chrisdavenport.mules.http4s.*
import org.http4s.*
import org.http4s.Header.ToRaw.modelledHeadersToRaw
import run.cosy.http.cache.TreeDirCache
import run.cosy.http.cache.ServerNotFound

case class Caching[F[_]: Concurrent: Clock, T](
    cache: TreeDirCache[F, CacheItem[T]],
    interpret: Response[F] => F[CachedResponse[T]],
    cacheType: CacheType
):

   def request[G[_]: FlatMap](
       app: Kleisli[G, Request[F], Response[F]],
       fk: F ~> G
   )(req: Request[F]): G[CachedResponse[T]] =
     if CacheRules.requestCanUseCached(req)
     then
        for
           cachedValue <- fk(cache.lookup(req.uri).recover { case _: ServerNotFound => None })
           now <- fk(HttpDate.current[F])
           out <- cachedValue match
            case None =>
              if CacheRules.onlyIfCached(req)
              then fk(CachedResponse.errorResponse[T](Status.GatewayTimeout).pure[F])
              else app.run(req).flatMap(resp => fk(withResponse(req, resp)))
            case Some(item) =>
              if CacheRules.cacheAgeAcceptable(req, item, now) then fk(item.response.pure[F])
              else
                 app.run(
                   req.putHeaders(
                     CacheRules.getIfMatch(item.response).map(modelledHeadersToRaw(_)).toSeq*
                   ).putHeaders(
                     CacheRules.getIfUnmodifiedSince(item.response).map(modelledHeadersToRaw(_))
                       .toSeq*
                   )
                 ).flatMap(resp => fk(withResponse(req, resp)))
        yield out
     else app.run(req).flatMap(resp => fk(withResponse(req, resp)))
   end request

   private def withResponse(
       req: Request[F],
       resp: Response[F]
   ): F[CachedResponse[T]] = {
     if CacheRules.shouldInvalidate(req, resp) then cache.delete(req.uri)
     else Applicative[F].unit
   } *> {
     if CacheRules.isCacheable(req, resp, cacheType) then
        for
           cachedResp <- resp.status match
            case Status.NotModified => cache.lookup(req.uri).flatMap(
                _.map { item =>
                   val cached = item.response
                   cached.withHeaders(resp.headers ++ cached.headers).pure[F]
                }.getOrElse(interpret(resp))
              )
            case _ => interpret(resp)
           now <- HttpDate.current[F]
           expires = CacheRules.FreshnessAndExpiration.getExpires(now, resp)
           item <- CacheItem.create(cachedResp, expires.some)
           _ <- cache.insert(req.uri, item)
        yield cachedResp
     else interpret(resp)
   }
   end withResponse

end Caching
