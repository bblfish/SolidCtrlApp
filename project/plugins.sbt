// https://www.selenium.dev
// selenium testing
// libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "1.1.1"

// dom lib for nodejs as per https://www.scala-js.org/doc/tutorial/basic/
//libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
// but we try the  fork from https://github.com/exoego/scala-js-env-jsdom-nodejs
//libraryDependencies += "net.exoego" %% "scalajs-env-jsdom-nodejs" % "2.1.0" cross CrossVersion.for3Use2_13

//fix bug https://github.com/scala-js/scala-js-js-envs/issues/12
//this line should be removed with scalajs 1.8.0
//libraryDependencies += "org.scala-js" %% "scalajs-env-nodejs" % "1.2.1"

//https://search.maven.org/search?q=a:sbt-scalajs-bundler
//https://scalacenter.github.io/scalajs-bundler/
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.0")

// https://typelevel.org/sbt-typelevel/index.html
// sbt-typelevel configures sbt for developing, testing, cross-building, publishing, and documenting your Scala library on GitHub
addSbtPlugin("org.typelevel" % "sbt-typelevel" % "0.5.0-M5")

// https://github.com/DavidGregory084/sbt-tpolecat
// replaced by stb-typelevel
// addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"             % "0.1.20")

// http://www.scala-js.org/doc/tutorial/basic/
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.11.0")

// https://scalablytyped.org/docs/plugin
// addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta36")
