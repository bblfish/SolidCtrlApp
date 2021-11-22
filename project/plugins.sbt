// (dom lib for nodejs) for a fork see
// https://github.com/exoego/scala-js-env-jsdom-nodejs
//libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
libraryDependencies += "net.exoego" %% "scalajs-env-jsdom-nodejs" % "2.1.0" cross CrossVersion.for3Use2_13

addSbtPlugin("org.scala-js"              % "sbt-scalajs"              % "1.7.1")

//https://search.maven.org/search?q=a:sbt-scalajs-bundler
//https://scalacenter.github.io/scalajs-bundler/
addSbtPlugin("ch.epfl.scala"             % "sbt-scalajs-bundler"      % "0.21.0-RC1")

// https://github.com/DavidGregory084/sbt-tpolecat
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"             % "0.1.20")

// https://scalablytyped.org/docs/plugin
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta36")
