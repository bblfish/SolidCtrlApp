organization := "cosy.run"
name := "SolidApp"
version := "0.1.0"
scalaVersion := "3.1.0"

import org.scalajs.jsenv.nodejs.NodeJSEnv



//val outwatchVersion = "0.11.1-SNAPSHOT"
resolvers += "jitpack" at "https://jitpack.io"
//libraryDependencies ++= Seq(
//  ("io.github.outwatch" %%% "outwatch" % outwatchVersion) cross CrossVersion.for3Use2_13,
//  "org.scalatest" %%% "scalatest" % "3.2.9" % Test
//)

lazy val app = project.in(file("app"))
	.enablePlugins(ScalaJSBundlerPlugin)
	.settings(
	// https://github.com/http4s/http4s-dom
		libraryDependencies += "org.http4s" %%% "http4s-dom" % "1.0.0-M29",
		useYarn := true, // makes scalajs-bundler use yarn instead of npm
		Test / requireJsDomEnv := true,
		scalaJSUseMainModuleInitializer := true,
		scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script

		// https://github.com/rdfjs/N3.js/
		// do I also need to run:q `npm install n3` ?
		Compile / npmDependencies += "n3" -> "1.11.2",
		Test / npmDependencies += "n3" -> "1.11.2",
		jsEnv := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--dns-result-order=ipv4first"))),

	)
val n3jsFile = Path("n3js").asFile.getAbsoluteFile()
lazy val n3js = project.in(n3jsFile)
	.enablePlugins(ScalablyTypedConverterGenSourcePlugin)
	.settings(
		stUseScalaJsDom := true,
		stSourceGenMode := SourceGenMode.Manual(n3jsFile/"src"/"main"/"scala"),
		useYarn := true, // makes scalajs-bundler use yarn instead of npm
		Compile / npmDependencies += "@types/n3" -> "1.10.3",
		stMinimize := Selection.AllExcept("n3"),
		stOutputPackage := "n3js",
	)
	







// hot reloading configuration:
// https://github.com/scalacenter/scalajs-bundler/issues/180
addCommandAlias("dev", "; compile; fastOptJS::startWebpackDevServer; devwatch; fastOptJS::stopWebpackDevServer")
addCommandAlias("devwatch", "~; fastOptJS; copyFastOptJS")

// https://webpack.github.io
webpack / version := "4.46.0"
// https://webpack.js.org/configuration/dev-server/
startWebpackDevServer / version := "3.11.2"
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
