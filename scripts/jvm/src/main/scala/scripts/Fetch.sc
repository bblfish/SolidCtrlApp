
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

val priv = """-----BEGIN RSA PRIVATE KEY-----
  |MIIEqAIBAAKCAQEAhAKYdtoeoy8zcAcR874L8cnZxKzAGwd7v36APp7Pv6Q2jdsP
  |BRrwWEBnez6d0UDKDwGbc6nxfEXAy5mbhgajzrw3MOEt8uA5txSKobBpKDeBLOsd
  |JKFqMGmXCQvEG7YemcxDTRPxAleIAgYYRjTSd/QBwVW9OwNFhekro3RtlinV0a75
  |jfZgkne/YiktSvLG34lw2zqXBDTC5NHROUqGTlML4PlNZS5Ri2U4aCNx2rUPRcKI
  |lE0PuKxI4T+HIaFpv8+rdV6eUgOrB2xeI1dSFFn/nnv5OoZJEIB+VmuKn3DCUcCZ
  |SFlQPSXSfBDiUGhwOw76WuSSsf1D4b/vLoJ10wIDAQABAoIBAG/JZuSWdoVHbi56
  |vjgCgkjg3lkO1KrO3nrdm6nrgA9P9qaPjxuKoWaKO1cBQlE1pSWp/cKncYgD5WxE
  |CpAnRUXG2pG4zdkzCYzAh1i+c34L6oZoHsirK6oNcEnHveydfzJL5934egm6p8DW
  |+m1RQ70yUt4uRc0YSor+q1LGJvGQHReF0WmJBZHrhz5e63Pq7lE0gIwuBqL8SMaA
  |yRXtK+JGxZpImTq+NHvEWWCu09SCq0r838ceQI55SvzmTkwqtC+8AT2zFviMZkKR
  |Qo6SPsrqItxZWRty2izawTF0Bf5S2VAx7O+6t3wBsQ1sLptoSgX3QblELY5asI0J
  |YFz7LJECgYkAsqeUJmqXE3LP8tYoIjMIAKiTm9o6psPlc8CrLI9CH0UbuaA2JCOM
  |cCNq8SyYbTqgnWlB9ZfcAm/cFpA8tYci9m5vYK8HNxQr+8FS3Qo8N9RJ8d0U5Csw
  |DzMYfRghAfUGwmlWj5hp1pQzAuhwbOXFtxKHVsMPhz1IBtF9Y8jvgqgYHLbmyiu1
  |mwJ5AL0pYF0G7x81prlARURwHo0Yf52kEw1dxpx+JXER7hQRWQki5/NsUEtv+8RT
  |qn2m6qte5DXLyn83b1qRscSdnCCwKtKWUug5q2ZbwVOCJCtmRwmnP131lWRYfj67
  |B/xJ1ZA6X3GEf4sNReNAtaucPEelgR2nsN0gKQKBiGoqHWbK1qYvBxX2X3kbPDkv
  |9C+celgZd2PW7aGYLCHq7nPbmfDV0yHcWjOhXZ8jRMjmANVR/eLQ2EfsRLdW69bn
  |f3ZD7JS1fwGnO3exGmHO3HZG+6AvberKYVYNHahNFEw5TsAcQWDLRpkGybBcxqZo
  |81YCqlqidwfeO5YtlO7etx1xLyqa2NsCeG9A86UjG+aeNnXEIDk1PDK+EuiThIUa
  |/2IxKzJKWl1BKr2d4xAfR0ZnEYuRrbeDQYgTImOlfW6/GuYIxKYgEKCFHFqJATAG
  |IxHrq1PDOiSwXd2GmVVYyEmhZnbcp8CxaEMQoevxAta0ssMK3w6UsDtvUvYvF22m
  |qQKBiD5GwESzsFPy3Ga0MvZpn3D6EJQLgsnrtUPZx+z2Ep2x0xc5orneB5fGyF1P
  |WtP+fG5Q6Dpdz3LRfm+KwBCWFKQjg7uTxcjerhBWEYPmEMKYwTJF5PBG9/ddvHLQ
  |EQeNC8fHGg4UXU8mhHnSBt3EA10qQJfRDs15M38eG2cYwB1PZpDHScDnDA0=
  |-----END RSA PRIVATE KEY-----""".stripMargin

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
