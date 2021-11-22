import Dependencies._

organization := "cosy.run"
name := "SolidApp"
version := "0.1.0"
scalaVersion := Ver.scala

import org.scalajs.jsenv.nodejs.NodeJSEnv

jsEnv := new net.exoego.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
//jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

lazy val commonSettings = Seq(
   name := "Solid App",
   version := "0.1-SNAPSHOT",
   description := "Solid App",
   startYear := Some(2021),
   scalaVersion := Ver.scala,
   updateOptions := updateOptions.value.withCachedResolution(true) //to speed up dependency resolution
)

val scala3jsOptions =  Seq(
   // "-classpath", "foo:bar:...",         // Add to the classpath.
   //"-encoding", "utf-8",                // Specify character encoding used by source files.
   "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
   "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
   "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
   //"-explain",                          // Explain errors in more detail.
   //"-explain-types",                    // Explain type errors in more detail.
   "-indent",                           // Together with -rewrite, remove {...} syntax when possible due to significant indentation.
   // "-no-indent",                        // Require classical {...} syntax, indentation is not significant.
   "-new-syntax",                       // Require `then` and `do` in control expressions.
   // "-old-syntax",                       // Require `(...)` around conditions.
   // "-language:Scala2",                  // Compile Scala 2 code, highlight what needs updating
   //"-language:strictEquality",          // Require +derives Eql+ for using == or != comparisons
   // "-rewrite",                          // Attempt to fix code automatically. Use with -indent and ...-migration.
   // "-scalajs",                          // Compile in Scala.js mode (requires scalajs-library.jar on the classpath).
   "-source:future",                       // Choices: future and future-migration. I use this to force future deprecation warnings, etc.
   // "-Xfatal-warnings",                  // Fail on warnings, not just errors
   // "-Xmigration",                       // Warn about constructs whose behavior may have changed since version.
   // "-Ysafe-init",                       // Warn on field access before initialization
   "-Yexplicit-nulls"                  // For explicit nulls behavior.
)

//val outwatchVersion = "0.11.1-SNAPSHOT"
resolvers += "jitpack" at "https://jitpack.io"
//libraryDependencies ++= Seq(
//  ("io.github.outwatch" %%% "outwatch" % outwatchVersion) cross CrossVersion.for3Use2_13,
//  "org.scalatest" %%% "scalatest" % "3.2.9" % Test
//)

lazy val app = project.in(file("app"))
   // note enabling the bundler changes what js needs to be called https://github.com/scalacenter/scalajs-bundler/issues/193
	// .enablePlugins(ScalaJSBundlerPlugin)
	.enablePlugins(ScalaJSPlugin)
	//perhaps instead if the ScalaJSPlugin I should use https://github.com/vmunier/sbt-web-scalajs
	.settings(commonSettings:_*)
	.settings(
		description := "The Solid App",
		// https://github.com/http4s/http4s-dom
		libraryDependencies += "org.http4s" %%% "http4s-dom" % "1.0.0-M29",
		// libraryDependencies += "n3js" %%% "n3js" % "0.1-SNAPSHOT",
		libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0",
		resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
		// useYarn := true, // makes scalajs-bundler use yarn instead of npm
		Test / requireJsDomEnv := true,
		scalaJSUseMainModuleInitializer := true,
		// with ComminJSModule set one gets "exports is not defined" problem when just trying to call js from html
		// scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script
		// scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }, //does not work because we are using ScalaJSBundlerPlugin
		jsEnv := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--dns-result-order=ipv4first"))),

		// this is only needed for run, should be moved to tests when tests are working
		// Compile / npmDependencies += "node-fetch" -> "3.1.0",
		// Compile / npmDependencies += "jsdom" -> "18.1.1",

		// https://webpack.github.io
		// https://github.com/webpack/webpack
		// webpack 5 should be supported https://github.com/scalacenter/scalajs-bundler/issues/350
		//webpack / version := "5.64.2",
		// https://webpack.js.org/configuration/dev-server/
		// startWebpackDevServer / version := "4.5.0",

		// https://github.com/scalacenter/scalajs-bundler/issues/385#issuecomment-756243511
		webpackEmitSourceMaps := false,

		webpackDevServerExtraArgs := Seq("--color"),
		webpackDevServerPort := 8080,
		fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),
		fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(), // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance
	   //emitSourceMaps := false
	).dependsOn(n3js)

lazy val n3jsDir = Path("n3js").asFile.getAbsoluteFile()
//project to use when we want to create code from the TypeScript Template for N3
// https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/n3
// lazy val n3jsST = project.in(n3jsDir/"ST")
// 	.enablePlugins(ScalablyTypedConverterGenSourcePlugin)
// 	.settings(
// 		name := "n3js Scalably Typed project",
// 		stUseScalaJsDom := true,
// 		stSourceGenMode := SourceGenMode.Manual(n3jsDir/"src"/"main"/"scala"),
// 		useYarn := true, // makes scalajs-bundler use yarn instead of npm
// 		Compile / npmDependencies += "@types/n3" -> "1.10.3",
// 		stMinimize := Selection.AllExcept("n3"),
// 		stOutputPackage := "n3js",
// 	)

lazy val n3js = project.in(n3jsDir)
	.settings(commonSettings:_*)
	.enablePlugins(ScalaJSPlugin)
	.enablePlugins(ScalaJSBundlerPlugin)
	.settings(
		name := "N3js",
      // scalacOptions := scala3jsOptions,
		libraryDependencies ++= Seq(
		// https://github.com/http4s/http4s-dom
		//https://search.maven.org/artifact/org.http4s/http4s-dom_sjs1_3/1.0.0-M29/jar
		"org.http4s" %%% "http4s-dom" % "1.0.0-M29",
		"net.bblfish.rdf" %%% "rdf-model-js" % "0.1-SNAPSHOT"
		),
		resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
		// useYarn := true, // makes scalajs-bundler use yarn instead of npm
		// Test / requireJsDomEnv := true,
		// scalaJSUseMainModuleInitializer := true,
		scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script

		// https://github.com/rdfjs/N3.js/
		// do I also need to run `npm install n3` ?
		Compile / npmDependencies += "n3" -> "1.11.2",
		Test / npmDependencies += "n3" -> "1.11.2",
		jsEnv := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--dns-result-order=ipv4first"))),
	)	

// hot reloading configuration:
// https://github.com/scalacenter/scalajs-bundler/issues/180
// addCommandAlias("dev", "; compile; fastOptJS::startWebpackDevServer; devwatch; fastOptJS::stopWebpackDevServer")
//addCommandAlias("devwatch", "~; fastOptJS; copyFastOptJS")

// when running the "dev" alias, after every fastOptJS compile all artifacts are copied into
// a folder which is served and watched by the webpack devserver.
// this is a workaround for: https://github.com/scalacenter/scalajs-bundler/issues/180
// lazy val copyFastOptJS = TaskKey[Unit]("copyFastOptJS", "Copy javascript files to target directory")
// copyFastOptJS := {
//   val inDir = (Compile / fastOptJS / crossTarget).value
//   val outDir = (Compile / fastOptJS / crossTarget).value / "dev"
//   val files = Seq(name.value.toLowerCase + "-fastopt-loader.js", name.value.toLowerCase + "-fastopt.js") map { p => (inDir / p, outDir / p) }
//   IO.copy(files, overwrite = true, preserveLastModified = true, preserveExecutable = true)
// }
