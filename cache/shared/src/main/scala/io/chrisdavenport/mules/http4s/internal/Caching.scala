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
