/*
 * Copyright 2021 Typelevel
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

package scripts

import _root_.io.lemonlabs.uri as ll
import bobcats.util.BouncyJavaPEMUtils.getPrivateKeySpec
import bobcats.{AsymmetricKeyAlg, PKCS8KeySpec, Signer, Verifier}
import cats.effect.*
import cats.effect.unsafe.IORuntime
import net.bblfish.app.auth.AuthNClient
import net.bblfish.wallet.{BasicId, BasicWallet, KeyData, WalletTools}
import org.w3.banana.jena.JenaRdf
import org.w3.banana.jena.JenaRdf.ops
import ops.given
import org.http4s.Uri as H4Uri
import org.w3.banana.jena.JenaRdf.R
import run.cosy.http.headers.Rfc8941
import run.cosy.ld.http4s.RDFDecoders
import scodec.bits.ByteVector
import org.http4s.client.*
import org.http4s.ember.client.*
import run.cosy.http.headers.SigIn.KeyId
import org.w3.banana.RDF
import org.w3.banana.Ops
import run.cosy.http.cache.TreeDirCache.WebCache
import io.chrisdavenport.mules.http4s.CacheItem
import run.cosy.http.cache.TreeDirCache
import run.cosy.http.cache.InterpretedCacheMiddleware
import org.http4s.Response
import io.chrisdavenport.mules.http4s.CachedResponse
import fs2.io.net.Network

object AnHttpSigClient:
   implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

   val priv = """-----BEGIN PRIVATE KEY-----
    MIIEvgIBADALBgkqhkiG9w0BAQoEggSqMIIEpgIBAAKCAQEAr4tmm3r20Wd/Pbqv
    P1s2+QEtvpuRaV8Yq40gjUR8y2Rjxa6dpG2GXHbPfvMs8ct+Lh1GH45x28Rw3Ry5
    3mm+oAXjyQ86OnDkZ5N8lYbggD4O3w6M6pAvLkhk95AndTrifbIFPNU8PPMO7Oyr
    FAHqgDsznjPFmTOtCEcN2Z1FpWgchwuYLPL+Wokqltd11nqqzi+bJ9cvSKADYdUA
    AN5WUtzdpiy6LbTgSxP7ociU4Tn0g5I6aDZJ7A8Lzo0KSyZYoA485mqcO0GVAdVw
    9lq4aOT9v6d+nb4bnNkQVklLQ3fVAvJm+xdDOp9LCNCN48V2pnDOkFV6+U9nV5oy
    c6XI2wIDAQABAoIBAQCUB8ip+kJiiZVKF8AqfB/aUP0jTAqOQewK1kKJ/iQCXBCq
    pbo360gvdt05H5VZ/RDVkEgO2k73VSsbulqezKs8RFs2tEmU+JgTI9MeQJPWcP6X
    aKy6LIYs0E2cWgp8GADgoBs8llBq0UhX0KffglIeek3n7Z6Gt4YFge2TAcW2WbN4
    XfK7lupFyo6HHyWRiYHMMARQXLJeOSdTn5aMBP0PO4bQyk5ORxTUSeOciPJUFktQ
    HkvGbym7KryEfwH8Tks0L7WhzyP60PL3xS9FNOJi9m+zztwYIXGDQuKM2GDsITeD
    2mI2oHoPMyAD0wdI7BwSVW18p1h+jgfc4dlexKYRAoGBAOVfuiEiOchGghV5vn5N
    RDNscAFnpHj1QgMr6/UG05RTgmcLfVsI1I4bSkbrIuVKviGGf7atlkROALOG/xRx
    DLadgBEeNyHL5lz6ihQaFJLVQ0u3U4SB67J0YtVO3R6lXcIjBDHuY8SjYJ7Ci6Z6
    vuDcoaEujnlrtUhaMxvSfcUJAoGBAMPsCHXte1uWNAqYad2WdLjPDlKtQJK1diCm
    rqmB2g8QE99hDOHItjDBEdpyFBKOIP+NpVtM2KLhRajjcL9Ph8jrID6XUqikQuVi
    4J9FV2m42jXMuioTT13idAILanYg8D3idvy/3isDVkON0X3UAVKrgMEne0hJpkPL
    FYqgetvDAoGBAKLQ6JZMbSe0pPIJkSamQhsehgL5Rs51iX4m1z7+sYFAJfhvN3Q/
    OGIHDRp6HjMUcxHpHw7U+S1TETxePwKLnLKj6hw8jnX2/nZRgWHzgVcY+sPsReRx
    NJVf+Cfh6yOtznfX00p+JWOXdSY8glSSHJwRAMog+hFGW1AYdt7w80XBAoGBAImR
    NUugqapgaEA8TrFxkJmngXYaAqpA0iYRA7kv3S4QavPBUGtFJHBNULzitydkNtVZ
    3w6hgce0h9YThTo/nKc+OZDZbgfN9s7cQ75x0PQCAO4fx2P91Q+mDzDUVTeG30mE
    t2m3S0dGe47JiJxifV9P3wNBNrZGSIF3mrORBVNDAoGBAI0QKn2Iv7Sgo4T/XjND
    dl2kZTXqGAk8dOhpUiw/HdM3OGWbhHj2NdCzBliOmPyQtAr770GITWvbAI+IRYyF
    S7Fnk6ZVVVHsxjtaHy1uJGFlaZzKR4AGNaUTOJMs6NadzCmGPAxNQQOCqoUjn4XR
    rOjr9w349JooGXhOxbu8nOxX
    -----END PRIVATE KEY-----"""

   lazy val pkcs8K: PKCS8KeySpec[AsymmetricKeyAlg] =
     getPrivateKeySpec(priv, AsymmetricKeyAlg.RSA_PSS_Key).get

   val keyIdStr = "http://localhost:8080/rfcKey#"
   // val keyUrl: ll.Url = ll.Url("http://127.0.0.1:8080/rfcKey")
   val keyUrl = URI(keyIdStr)
   lazy val signerF: IO[ByteVector => IO[ByteVector]] = Signer[IO]
     .build(pkcs8K, bobcats.AsymmetricKeyAlg.`rsa-pss-sha512`)

   lazy val keyIdData: KeyData[cats.effect.IO] =
     new KeyData[IO](KeyId(Rfc8941.SfString(keyIdStr)), signerF)
   import org.w3.banana.jena.io.JenaRDFWriter.given
   import org.w3.banana.jena.io.JenaRDFReader.given

   import org.http4s.syntax.all.uri

   given rdfDecoders: RDFDecoders[IO, R] = RDFDecoders[IO, R]
   def ioStr(uri: H4Uri, client: Client[IO]): IO[String] = ClientTools
     .authClient[IO, R](keyIdData, client).flatMap(_.expect[String](uri))

   // run "use" on this to get a client
   def emberClient: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

   def emberAuthClient: IO[Client[IO]] = emberClient.use { client =>
     ClientTools.authClient(keyIdData, client)
   }

   @main
   def fetch(uriStr: String = "http://localhost:8080/protected/README"): Unit =
      // ioStr(uri"http://localhost:8080/").unsafeRunSync()
      // ioStr(uri"http://localhost:8080/protected/").unsafeRunSync()
      val result = emberAuthClient.flatMap { client =>
        ioStr(H4Uri.unsafeFromString(uriStr), client)
      }.unsafeRunSync()
      println(result)

end AnHttpSigClient

object ClientTools:
   import cats.effect.kernel.Clock
   import cats.effect.Concurrent
   import cats.syntax.all.*

   /** enhance client so that it logs and can authenticate with given keyId, and save acls to graph
     * cache it creates. (a bit adhoc of a function, but it is a script)
     */
   def authClient[F[_]: Clock: Async, R <: RDF](
       keyIdData: KeyData[F],
       client: Client[F]
   )(using
       ops: Ops[R],
       rdfDecoders: RDFDecoders[F, R]
   ): F[Client[F]] =
      import org.http4s.client.middleware.Logger
      val loggedClient: Client[F] = Logger[F](
        true,
        true,
        logAction = Some(str => Concurrent[F].pure(System.out.println(str)))
      )(client)
      val walletTools: WalletTools[R] = new WalletTools[R]
      for ref <- Ref.of[F, WebCache[CacheItem[RDF.rGraph[R]]]](Map.empty)
      yield
         val cache = TreeDirCache[F, CacheItem[RDF.rGraph[R]]](ref)
         val interClientMiddleware = walletTools.cachedRelGraphMiddleware(cache)
         val bw = new BasicWallet[F, R](
           Map(),
           Seq(keyIdData)
         )(interClientMiddleware(loggedClient))
         AuthNClient[F](bw)(loggedClient)
