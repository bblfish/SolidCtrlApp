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

package mules

object test:
   import cats.*
   import cats.implicits.*
   import cats.effect.*
   import io.chrisdavenport.mules.*
   import io.chrisdavenport.mules.caffeine.*
   import io.chrisdavenport.mules.http4s.*
   import org.http4s.*
   import org.http4s.implicits.*
   import org.http4s.client.Client
   import org.http4s.ember.client.EmberClientBuilder

   def testMiddleware[F[_]: Concurrent](c: Client[F], ref: Ref[F, Int]): Client[F] = Client { req =>
     c.run(req).evalMap(resp => ref.update(_ + 1).as(resp))
   }

   val jQueryRequest =
     Request[IO](Method.GET, uri"https://code.jquery.com/jquery-3.4.1.slim.min.js")
   // jQueryRequest: Request[[A >: Nothing <: Any] => IO[A]] = (
   //    = GET,
   //    = Uri(
   //     scheme = Some(value = Scheme(https)),
   //     authority = Some(
   //       value = Authority(
   //         userInfo = None,
   //         host = RegName(host = code.jquery.com),
   //         port = None
   //       )
   //     ),
   //     path = /jquery-3.4.1.slim.min.js,
   //     query = ,
   //     fragment = None
   //   ),
   //    = HttpVersion(major = 1, minor = 1),
   //    = Headers(),
   //    = Stream(..),
   //    = org.typelevel.vault.Vault@7662cebd
   // )

   val exampleCached: IO[(Int, Int)] = EmberClientBuilder.default[IO].build.use { client =>
     for
        cache <- CaffeineCache.build[IO, (Method, Uri), CacheItem](None, None, 10000L.some)
        counter <- Ref[IO].of(0)
        cacheMiddleware = CacheMiddleware.client(cache, CacheType.Public)
        finalClient = cacheMiddleware(testMiddleware(client, counter))
        _ <- finalClient.run(jQueryRequest).use(_.as[String])
        count1 <- counter.get
        _ <- finalClient.run(jQueryRequest).use(_.as[String])
        count2 <- counter.get
     yield (count1, count2)
   }
   // exampleCached: IO[Tuple2[Int, Int]] = FlatMap(
   //   ioe = Attempt(
   //     ioa = Map(
   //       ioe = Blocking(
   //         hint = Blocking,
   //         thunk = fs2.io.net.tls.TLSContextCompanionPlatform$BuilderCompanionPlatform$AsyncBuilder$$Lambda$12570/0x00000008033a0440@72b94e70,
   //         event = cats.effect.tracing.TracingEvent$StackTrace
   //       ),
   //       f = fs2.io.net.tls.TLSContextCompanionPlatform$BuilderCompanionPlatform$AsyncBuilder$$Lambda$12572/0x00000008033a2040@1aed8e3c,
   //       event = cats.effect.tracing.TracingEvent$StackTrace
   //     )
   //   ),
   //   f = cats.effect.kernel.Resource$$Lambda$12577/0x00000008033a6040@7a9abcfa,
   //   event = cats.effect.tracing.TracingEvent$StackTrace
   // )

   import cats.effect.unsafe.implicits.global // DON'T DO THIS IN PROD // DON'T DO THIS IN PROD

   exampleCached.unsafeRunSync()
   // res0: Tuple2[Int, Int] = (1, 1)

   val dadJokesRequest = Request[IO](Method.GET, uri"https://icanhazdadjoke.com/")
   // dadJokesRequest: Request[[A >: Nothing <: Any] => IO[A]] = (
   //    = GET,
   //    = Uri(
   //     scheme = Some(value = Scheme(https)),
   //     authority = Some(
   //       value = Authority(
   //         userInfo = None,
   //         host = RegName(host = icanhazdadjoke.com),
   //         port = None
   //       )
   //     ),
   //     path = /,
   //     query = ,
   //     fragment = None
   //   ),
   //    = HttpVersion(major = 1, minor = 1),
   //    = Headers(),
   //    = Stream(..),
   //    = org.typelevel.vault.Vault@719e680c
   // )

   val exampleUnCached = EmberClientBuilder.default[IO].build.use { client =>
     for
        cache <- CaffeineCache.build[IO, (Method, Uri), CacheItem](None, None, 10000L.some)
        counter <- Ref[IO].of(0)
        cacheMiddleware = CacheMiddleware.client(cache, CacheType.Public)
        finalClient = cacheMiddleware(testMiddleware(client, counter))
        _ <- finalClient.run(dadJokesRequest).use(_.as[String])
        count1 <- counter.get
        _ <- finalClient.run(dadJokesRequest).use(_.as[String])
        count2 <- counter.get
     yield (count1, count2)
   }
   // exampleUnCached: IO[Tuple2[Int, Int]] = FlatMap(
   //   ioe = Attempt(
   //     ioa = Map(
   //       ioe = Blocking(
   //         hint = Blocking,
   //         thunk = fs2.io.net.tls.TLSContextCompanionPlatform$BuilderCompanionPlatform$AsyncBuilder$$Lambda$12570/0x00000008033a0440@72b94e70,
   //         event = cats.effect.tracing.TracingEvent$StackTrace
   //       ),
   //       f = fs2.io.net.tls.TLSContextCompanionPlatform$BuilderCompanionPlatform$AsyncBuilder$$Lambda$12572/0x00000008033a2040@5a74133e,
   //       event = cats.effect.tracing.TracingEvent$StackTrace
   //     )
   //   ),
   //   f = cats.effect.kernel.Resource$$Lambda$12577/0x00000008033a6040@506cf910,
   //   event = cats.effect.tracing.TracingEvent$StackTrace
   // )

   exampleUnCached.unsafeRunSync()
// res1: Tuple2[Int, Int] = (1, 2)
