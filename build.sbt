import sbt.ThisBuild
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import Dependencies._

name := "SolidApp"
ThisBuild / organization := "net.bblfish"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := Ver.scala
ThisBuild / startYear := Some(2021)
ThisBuild / developers := List(
  tlGitHubDev("bblfish", "Henry Story")
)

//import org.scalajs.jsenv.nodejs.NodeJSEnv

lazy val commonSettings = Seq(
  name := "Solid App",
  version := "0.1-SNAPSHOT",
  description := "Solid App",
  startYear := Some(2021),
  scalaVersion := Ver.scala,
  updateOptions := updateOptions.value.withCachedResolution(
    true
  ) // to speed up dependency resolution
)

val scala3jsOptions = Seq(
  // "-classpath", "foo:bar:...",         // Add to the classpath.
  // "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  // "-explain",                          // Explain errors in more detail.
  // "-explain-types",                    // Explain type errors in more detail.
  "-indent", // Together with -rewrite, remove {...} syntax when possible due to significant indentation.
  // "-no-indent",                        // Require classical {...} syntax, indentation is not significant.
  "-new-syntax", // Require `then` and `do` in control expressions.
  // "-old-syntax",                       // Require `(...)` around conditions.
  // "-language:Scala2",                  // Compile Scala 2 code, highlight what needs updating
  // "-language:strictEquality",          // Require +derives Eql+ for using == or != comparisons
  // "-rewrite",                          // Attempt to fix code automatically. Use with -indent and ...-migration.
  // "-scalajs",                          // Compile in Scala.js mode (requires scalajs-library.jar on the classpath).
  "-source:future", // Choices: future and future-migration. I use this to force future deprecation warnings, etc.
  // "-Xfatal-warnings",                  // Fail on warnings, not just errors
  // "-Xmigration",                       // Warn about constructs whose behavior may have changed since version.
  // "-Ysafe-init",                       // Warn on field access before initialization
  "-Yexplicit-nulls" // For explicit nulls behavior.
)

//val outwatchVersion = "0.11.1-SNAPSHOT"
resolvers += "jitpack" at "https://jitpack.io"
//libraryDependencies ++= Seq(
//  ("io.github.outwatch" %%% "outwatch" % outwatchVersion) cross CrossVersion.for3Use2_13,
//  "org.scalatest" %%% "scalatest" % "3.2.9" % Test
//)

lazy val authN = crossProject(JVMPlatform) // , JSPlatform)
  .crossType(CrossType.Full)
  .in(file("authn"))
  .settings(commonSettings: _*)
  .settings(
    name := "AuthNClient",
    description := "Http Client Middleware that knows how to use a Wallet interface to authenticate",
    // scalacOptions := scala3jsOptions,
    resolvers += sonatypeSNAPSHOT,
    libraryDependencies ++= Seq(http4s.client.value),
    libraryDependencies ++= Seq(
      munit.value % Test,
      cats.munitEffect.value % Test,
      http4s.server.value % Test,
      http4s.client.value % Test,
      http4s.theDsl.value % Test
    )
//		// useYarn := true, // makes scalajs-bundler use yarn instead of npm
    // Test / requireJsDomEnv := true,
    // scalaJSUseMainModuleInitializer := true,
    // scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script
    // ESModule cannot be used because we are using ScalaJSBundlerPlugin
    // scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },

    //		fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),

    // https://github.com/rdfjs/N3.js/
    // do I also need to run `npm install n3` ?
    // Compile / npmDependencies += NPM.n3,
    //   Test / npmDependencies += NPM.n3
  )
  .dependsOn(wallet)

lazy val free = crossProject(JVMPlatform) // , JSPlatform)
  .crossType(CrossType.Full)
  .in(file("free"))
  .settings(commonSettings: _*)
  .settings(
    name := "Free LDP",
    description := "Free LDP client and provisionally interpreters (to be moved)",
    libraryDependencies ++= Seq(
      cats.core.value,
      cats.free.value,
      http4s.core.value
    )
  )

// an LDES client
lazy val ldes = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("ldes"))
  .dependsOn(ioExt4s)
  .settings(commonSettings: _*)
  .settings(
    name := "LDES Client",
    description := "Linked Data Event Stream Libraries and Client",
    // scalacOptions := scala3jsOptions,
    resolvers += sonatypeSNAPSHOT,
    libraryDependencies ++= Seq(
//      cats.effect.value,
      cats.fs2.value
    ),
    libraryDependencies ++= Seq(
      munit.value % Test,
      cats.munitEffect.value % Test,
      banana.bananaJena.value % Test
    )
  )

// todo: should be moved closer to banana-rdf repo
lazy val ioExt4s = crossProject(JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("ioExt4s"))
  .settings(commonSettings: _*)
  .settings(
    name := "IO http4s ext",
    description := "rdf io extensions for http4s",
    // scalacOptions := scala3jsOptions,
    resolvers += sonatypeSNAPSHOT,
    libraryDependencies ++= Seq(
      http4s.client.value,
      banana.bananaIO.value
    ),
    libraryDependencies ++= Seq(
      munit.value % Test,
      cats.munitEffect.value % Test
    )
  )

//todo: we should split the wallet into client-wallet and the full wallet library
// as clients of the wallet only need a minimal interface
lazy val wallet = crossProject(JVMPlatform) // , JSPlatform)
  .crossType(CrossType.Full)
  .in(file("wallet"))
  .dependsOn(ioExt4s)
  .settings(commonSettings: _*)
  .settings(
    name := "Solid Wallet",
    description := "Solid Wallet libraries	",
    // scalacOptions := scala3jsOptions,
    resolvers += sonatypeSNAPSHOT,
    libraryDependencies ++= Seq(
      http4s.client.value,
      http4s.ember_client.value, // <- remove. added to explore implementation
      banana.bananaJena.value,
      crypto.http4sSig.value
    ),
    libraryDependencies ++= Seq(
      munit.value % Test,
      cats.munitEffect.value % Test,
//      http4s.server.value % Test,
      http4s.client.value % Test,
      http4s.theDsl.value % Test
    )
    //		// useYarn := true, // makes scalajs-bundler use yarn instead of npm
    // Test / requireJsDomEnv := true,
    // scalaJSUseMainModuleInitializer := true,
    // scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script
    // ESModule cannot be used because we are using ScalaJSBundlerPlugin
    // scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },

    //		fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),

    // https://github.com/rdfjs/N3.js/
    // do I also need to run `npm install n3` ?
    // Compile / npmDependencies += NPM.n3,
    // Test / npmDependencies += NPM.n3
  )

lazy val scripts = crossProject(JVMPlatform)
  .in(file("scripts"))
//  .settings(
//    libraryDependencies ++= Seq(
//      other.scalaUri.value,
//      crypto.bobcats.value classifier ("tests"), // bobcats test examples,
//      crypto.bobcats.value classifier ("tests-sources") // bobcats test examples soources,
//    )
//  )
  .dependsOn(wallet, authN, ldes)
  .jvmSettings(
    libraryDependencies ++= Seq(
      crypto.bobcats.value classifier ("tests"), // bobcats test examples,
      crypto.bobcats.value classifier ("tests-sources"), // bobcats test examples soources,
      other.scalaUri.value,
      http4s.ember_client.value,
      crypto.nimbusJWT_JDK.value,
      crypto.bouncyJCA_JDK.value
    )
  )

//lazy val app = project
//  .in(file("app"))
//  // note enabling the bundler changes what js needs to be called https://github.com/scalacenter/scalajs-bundler/issues/193
//  // https://stackoverflow.com/questions/41904346/sscalajsmodulekind-modulekind-commonjsmodule-cannot-invoke-main-method-anym
//  // reference doc for Bundler: https://scalacenter.github.io/scalajs-bundler/reference.html
//  .enablePlugins(ScalaJSBundlerPlugin)
//  .enablePlugins(ScalaJSPlugin)
//  //perhaps instead if the ScalaJSPlugin I should use https://github.com/vmunier/sbt-web-scalajs
//  .settings(commonSettings: _*)
//  .settings(
//    description := "The Solid App",
//    libraryDependencies ++= Seq(
//      http4s.Dom.value,
//      scalajsDom.value,
//      bananaRdfLib.value
//    ),
//    libraryDependencies += munit.value % Test,
//
//    //	 	useYarn := true, // makes scalajs-bundler use yarn instead of npm
//
//    // with ComminJSModule set one gets "exports is not defined" problem when just trying to call js from html
//    // scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script
//    // scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }, //does not work because we are using ScalaJSBundlerPlugin
//
//    // this is only needed for run, should be moved to tests when tests are working
//    // Compile / npmDependencies += "node-fetch" -> "3.1.0",
//    Test / npmDependencies += NPM.jsDom,
//
//    // https://webpack.github.io
//    // https://github.com/webpack/webpack
//    // webpack 5 should be supported https://github.com/scalacenter/scalajs-bundler/issues/350
//    webpack / version := "5.64.2",
//    // https://webpack.js.org/configuration/dev-server/
//    // startWebpackDevServer / version := "4.5.0",
//
//    // https://github.com/scalacenter/scalajs-bundler/issues/385#issuecomment-756243511
//    webpackEmitSourceMaps := false,
//
//    // needed to be able to have multiple entry points
//    // https://scalacenter.github.io/scalajs-bundler/cookbook.html#several-entry-points
//    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
//
//    //in order to be able to use run in sbt shell we need both the following (and scalajs-env-jsdom-nodejs in plugins)
////		jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
//    //		 one could use the net.exoego fork? (then also change plugin)
//    jsEnv := new net.exoego.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
//    scalaJSUseMainModuleInitializer := true,
//
//    // https://scalacenter.github.io/scalajs-bundler/reference.html#jsdom
//    // it turns out that jsdDomEnv
//    Test / requireJsDomEnv := true,
//
//    // webpackDevServerExtraArgs := Seq("--color"),
//    // webpackDevServerPort := 8080,
//    fastOptJS / webpackConfigFile := Some(
//      baseDirectory.value / "webpack.config.dev.js"
//    )
//    // fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(), // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance
//  )
//.dependsOn(n3js)
//
// Move n3js to banana rdf !!
//
//lazy val n3jsDir = Path("n3js").asFile.getAbsoluteFile
////project to use when we want to create code from the TypeScript Template for N3
//// https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/n3
//// lazy val n3jsST = project.in(n3jsDir/"ST")
//// 	.enablePlugins(ScalablyTypedConverterGenSourcePlugin)
//// 	.settings(
//// 		name := "n3js Scalably Typed project",
//// 		stUseScalaJsDom := true,
//// 		stSourceGenMode := SourceGenMode.Manual(n3jsDir/"src"/"main"/"scala"),
//// 		useYarn := true, // makes scalajs-bundler use yarn instead of npm
//// 		Compile / npmDependencies += "@types/n3" -> "1.10.3",
//// 		stMinimize := Selection.AllExcept("n3"),
//// 		stOutputPackage := "n3js",
//// 	)
//
//lazy val n3js = project.in(n3jsDir)
//	.settings(commonSettings:_*)
//	.enablePlugins(ScalaJSPlugin)
//	.enablePlugins(ScalaJSBundlerPlugin)
//	.settings(
//		name := "N3js",
//      // scalacOptions := scala3jsOptions,
//		libraryDependencies ++= Seq(http4s.Dom.value, modelJS.value, munit.value),
//		resolvers += sonatypeSNAPSHOT,
//		// useYarn := true, // makes scalajs-bundler use yarn instead of npm
//		// Test / requireJsDomEnv := true,
//		// scalaJSUseMainModuleInitializer := true,
//		// scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script
//		// ESModule cannot be used because we are using ScalaJSBundlerPlugin
//		// scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
//
////		fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),
//
//		// https://github.com/rdfjs/N3.js/
//		// do I also need to run `npm install n3` ?
//		Compile / npmDependencies += NPM.n3,
//		Test / npmDependencies += NPM.n3,
//	)

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
