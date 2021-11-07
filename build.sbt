//organization := "cosy.run"
//name := "Solidapp"
//version := "0.1.0"

scalaVersion := "3.1.0"

val outwatchVersion = "e02749756f"
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies ++= Seq(
  ("com.github.outwatch.outwatch" %%% "outwatch" % outwatchVersion) cross CrossVersion.for3Use2_13,
  "org.scalatest" %%% "scalatest" % "3.2.9" % Test
)

enablePlugins(ScalaJSBundlerPlugin)
useYarn := true // makes scalajs-bundler use yarn instead of npm
Test / requireJsDomEnv := true
scalaJSUseMainModuleInitializer := true
scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)) // configure Scala.js to emit a JavaScript module instead of a top-level script

import org.scalajs.jsenv.nodejs.NodeJSEnv
jsEnv := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--dns-result-order=ipv4first")))

// hot reloading configuration:
// https://github.com/scalacenter/scalajs-bundler/issues/180
addCommandAlias("dev", "; compile; fastOptJS::startWebpackDevServer; devwatch; fastOptJS::stopWebpackDevServer")
addCommandAlias("devwatch", "~; fastOptJS; copyFastOptJS")

// https://webpack.github.io
webpack / version := "5.62.1"
// https://webpack.js.org/configuration/dev-server/
startWebpackDevServer / version := "4.4.0"
webpackDevServerExtraArgs := Seq("--color")
webpackDevServerPort := 8080
fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js")
fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly() // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance

// when running the "dev" alias, after every fastOptJS compile all artifacts are copied into
// a folder which is served and watched by the webpack devserver.
// this is a workaround for: https://github.com/scalacenter/scalajs-bundler/issues/180
lazy val copyFastOptJS = TaskKey[Unit]("copyFastOptJS", "Copy javascript files to target directory")
copyFastOptJS := {
  val inDir = (Compile / fastOptJS / crossTarget).value
  val outDir = (Compile / fastOptJS / crossTarget).value / "dev"
  val files = Seq(name.value.toLowerCase + "-fastopt-loader.js", name.value.toLowerCase + "-fastopt.js") map { p => (inDir / p, outDir / p) }
  IO.copy(files, overwrite = true, preserveLastModified = true, preserveExecutable = true)
}
