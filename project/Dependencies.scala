import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import sbt.{Def, *}

object Dependencies {
  object Ver {
    val scala = "3.2.2"
    val http4s = "1.0.0-M39"
    val banana = "0.9-c996591-SNAPSHOT"
    val bobcats = "0.3-3236e64-SNAPSHOT"
    val httpSig = "0.4-ac23f8b-SNAPSHOT"
  }
  object other {
    // https://github.com/lemonlabsuk/scala-uri
    val scalaUri = Def.setting("io.lemonlabs" %%% "scala-uri" % "4.0.3")
  }
  object mules {
    val core = Def.setting("io.chrisdavenport" %% "mules" % "0.7.0")
    val caffeine = Def.setting("io.chrisdavenport" %% "mules-caffeine" % "0.7.0")
    val http4s = Def.setting("io.chrisdavenport" %% "mules-http4s" % "0.4.0")
    // ember uses 0.23.18
    val ember_client = Def.setting("org.http4s" %%% "http4s-ember-client" % "0.23.18")
  }
  // https://http4s.org/v1.0/client/
  object http4s {
    def apply(packg: String): Def.Initialize[sbt.ModuleID] = Def
      .setting("org.http4s" %%% packg % Ver.http4s)
    lazy val core = http4s("http4s-core")
    lazy val client = http4s("http4s-client")
    // ember is an implementation of the client.
    lazy val ember_client = http4s("http4s-ember-client")
    lazy val server = http4s("http4s-server")
    lazy val theDsl = http4s("http4s-dsl")
    // https://github.com/http4s/http4s-dom
    // https://search.maven.org/artifact/org.http4s/http4s-dom_sjs1_3/1.0.0-M37/jar
    lazy val Dom = Def.setting("org.http4s" %%% "http4s-dom" % "1.0.0-M36")
  }
  object cats {
    lazy val core = Def.setting("org.typelevel" %%% "cats-core" % "2.9.0")
    lazy val free = Def.setting("org.typelevel" %%% "cats-free" % "2.9.0")
//    lazy val effect = Def.setting("org.typelevel" %% "cats-effect" % "3.4.8")
    // https://github.com/typelevel/fs2
    lazy val fs2 = Def.setting("co.fs2" %% "fs2-core" % "3.6.1")

    // https://github.com/typelevel/munit-cats-effect
    // https://search.maven.org/artifact/org.typelevel/munit-cats-effect_3/2.0.0-M3/jar
    lazy val munitEffect = Def.setting("org.typelevel" %%% "munit-cats-effect" % "2.0.0-M3")
  }
  object crypto {
    // https://oss.sonatype.org/content/repositories/snapshots/net/bblfish/crypto/bobcats_3/
    lazy val http4sSig = Def.setting(
      "net.bblfish.crypto" %%% "http4s-http-signature" % Ver.httpSig
    )
    lazy val nimbusJWT_JDK = Def.setting("com.nimbusds" % "nimbus-jose-jwt" % "9.25.4")
    lazy val bouncyJCA_JDK = Def.setting("org.bouncycastle" % "bcpkix-jdk18on" % "1.72")
    // https://oss.sonatype.org/content/repositories/snapshots/net/bblfish/crypto/bobcats_3/
    lazy val bobcats = Def.setting("net.bblfish.crypto" %%% "bobcats" % Ver.bobcats)
  }

  // not published yet
  object banana {
    lazy val bananaRdf = Def.setting("net.bblfish.rdf" %%% "banana-rdf" % Ver.banana)
    lazy val bananaIO = Def.setting("net.bblfish.rdf" %%% "banana-jena-io-sync" % Ver.banana)
    lazy val bananaJena = Def.setting("net.bblfish.rdf" %%% "banana-jena-io-sync" % Ver.banana)
  }
  val scalajsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % "2.0.0")
  val bananaRdfLib = Def.setting("net.bblfish.rdf" %%% "rdflibJS" % "0.9-SNAPSHOT")

  val munit = Def.setting("org.scalameta" %%% "munit" % "1.0.0-M7")
//  val utest = Def.setting("com.lihaoyi" %%% "utest" % "0.7.10")

//  val modelJS =
//    Def.setting("net.bblfish.rdf" %%% "rdf-model-js" % "0.2-dbfa81d-SNAPSHOT")
  // needed for modelJS
  val sonatypeSNAPSHOT: MavenRepository =
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  object NPM {
    val n3 = "n3" -> "1.11.2"
    val jsDom = "jsdom" -> "18.1.1"
  }

}
