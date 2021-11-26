import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Dependencies {
	object Ver {
		val scala = "3.1.0"
	}

	// https://github.com/http4s/http4s-dom
	//https://search.maven.org/artifact/org.http4s/http4s-dom_sjs1_3/1.0.0-M29/jar
	val http4sDom = Def.setting("org.http4s" %%% "http4s-dom" % "1.0.0-M29")

	val scalajsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % "2.0.0")
	val munit = Def.setting("org.scalameta" %%% "munit" % "1.0.0-M1" % Test)
	val utest = Def.setting("com.lihaoyi" %%% "utest" % "0.7.10" % Test)

	val modelJS = Def.setting("net.bblfish.rdf" %%% "rdf-model-js" % "0.1a-SNAPSHOT")
	//needed for modelJS
	val sonatypeSNAPSHOT: MavenRepository = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

	object NPM {
		val n3 = "n3" -> "1.11.2"
		val jsDom = "jsdom" -> "18.1.1"
	}
}
  

