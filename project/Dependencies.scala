import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt.{Def, _}

object Dependencies {
  object Ver {
    val scala = "3.1.3"
    val http4s = "1.0.0-M37"
    val banana = "0.9-7520bf7-20221011T214341Z-SNAPSHOT"
    val bobcats = "0.2-7a91946-SNAPSHOT"
    val httpSig = "0.2-e5ac965-SNAPSHOT"
  }

  object other {
    // https://github.com/lemonlabsuk/scala-uri
    val scalaUri = Def.setting("io.lemonlabs" %%% "scala-uri" % "4.0.2")
  }
  // https://http4s.org/v1.0/client/
  object http4s {
    def apply(packg: String): Def.Initialize[sbt.ModuleID] =
      Def.setting("org.http4s" %%% packg % Ver.http4s)
    lazy val core = http4s("http4s-core")
    lazy val client = http4s("http4s-client")
    // ember is an implementation of the client.
    lazy val ember_client = http4s("http4s-ember-client")
    lazy val server = http4s("http4s-server")
    lazy val theDsl = http4s("http4s-dsl")
    // https://github.com/http4s/http4s-dom
    // https://search.maven.org/artifact/org.http4s/http4s-dom_sjs1_3/1.0.0-M29/jar
    lazy val Dom = Def.setting("org.http4s" %%% "http4s-dom" % "1.0.0-M36")
  }

  object cats {
    // https://github.com/typelevel/munit-cats-effect
    lazy val munitEffect =
      Def.setting("org.typelevel" %%% "munit-cats-effect-3" % "1.0.7")
  }

  object crypto {
    // https://oss.sonatype.org/content/repositories/snapshots/net/bblfish/crypto/bobcats_3/
    lazy val http4sSig = Def.setting(
      "net.bblfish.crypto" %% "http4s-http-signature" % Ver.httpSig
    )
    lazy val nimbusJWT = Def.setting("com.nimbusds" % "nimbus-jose-jwt" % "9.25.4")
    lazy val bouncyJCA = Def.setting("org.bouncycastle" % "bcpkix-jdk18on" % "1.72")
    // https://oss.sonatype.org/content/repositories/snapshots/net/bblfish/crypto/bobcats_3/
    lazy val bobcats =
      Def.setting("net.bblfish.crypto" %%% "bobcats" % Ver.bobcats)
  }

  // not published yet
  object banana {
    lazy val bananaJena =
      Def.setting("net.bblfish.rdf" %% "banana-jena-io-sync" % Ver.banana)
  }

  val scalajsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % "2.0.0")
  val bananaRdfLib =
    Def.setting("net.bblfish.rdf" %%% "rdflibJS" % "0.9-SNAPSHOT")

  val munit = Def.setting("org.scalameta" %%% "munit" % "1.0.0-M1")
  val utest = Def.setting("com.lihaoyi" %%% "utest" % "0.7.10")

  val modelJS =
    Def.setting("net.bblfish.rdf" %%% "rdf-model-js" % "0.2-dbfa81d-SNAPSHOT")
  // needed for modelJS
  val sonatypeSNAPSHOT: MavenRepository =
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  object NPM {
    val n3 = "n3" -> "1.11.2"
    val jsDom = "jsdom" -> "18.1.1"
  }
}
