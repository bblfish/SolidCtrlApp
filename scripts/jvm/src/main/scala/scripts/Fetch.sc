
import bobcats.{AsymmetricKeyAlg, PKCS8KeySpec, Signer, Verifier}
import bobcats.util.BouncyJavaPEMUtils.getPrivateKeySpec
import net.bblfish.wallet.{BasicId, BasicWallet}
import _root_.io.lemonlabs.uri as ll
import cats.effect.IO
import cats.effect.*
import org.w3.banana.jena.JenaRdf
import org.w3.banana.jena.JenaRdf.ops
import ops.given
import org.w3.banana.jena.JenaRdf.R
import scodec.bits.ByteVector
import cats.effect.unsafe.IORuntime
import net.bblfish.app.auth.AuthNClient
import org.http4s.Uri as H4Uri
import org.w3.banana.http4sIO.RDFDecoders
implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
import org.http4s.ember.client._
import org.http4s.client._

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

val pkcs8K: PKCS8KeySpec[AsymmetricKeyAlg] = getPrivateKeySpec(priv,AsymmetricKeyAlg.RSA_PSS_Key).get
//val keyUrl: ll.Url = ll.Url("http://127.0.0.1:8080/rfcKey")
val keyUrl = URI("http://localhost:8080/rfcKey#")
val signerF: IO[ByteVector => IO[ByteVector]] = Signer[IO].build(pkcs8K, bobcats.AsymmetricKeyAlg.`rsa-pss-sha512`)
import org.w3.banana.jena.io.JenaRDFReader.given
import org.w3.banana.jena.io.JenaRDFWriter.given

val keyid = net.bblfish.wallet.KeyId[IO,R](keyUrl,signerF)

given dec: RDFDecoders[IO,R] = new RDFDecoders()
import org.http4s.syntax.all.uri

def ioStr(uri: H4Uri) = EmberClientBuilder.default[IO].build.use { (client: Client[IO] ) =>
  import org.http4s.client.middleware.Logger
  given loggedClient: Client[IO] = Logger[IO](true,true,logAction = Some(str => IO(System.out.println(str))))(client)
  val bw = BasicWallet[IO, R](
    Map(),
    Seq(keyid)
  )
  val newClient: Client[IO] = AuthNClient[IO].apply(bw)(loggedClient)
  newClient.expect[String](uri)
}
//ioStr(uri"http://localhost:8080/").unsafeRunSync()
//ioStr(uri"http://localhost:8080/protected/").unsafeRunSync()
ioStr(uri"http://localhost:8080/protected/README").unsafeRunSync()
