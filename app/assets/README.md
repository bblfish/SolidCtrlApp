Depending on how `build.sbt` is setup, two different 
files should be used.

## 1. No ScalaJSBundlerPlugin

If no ScalaJSBunderPlugin is set then the files generated on calling 
```scala
> fastLinkJS
```
will appear as `main.js` in a subdirectory of `scala-3.1.0`.
This is what `noPlugin.html` uses.

## 2. With ScalaJSBundlerPlugin

With `ScalaJSBundlerPlugin` set the code is generated using 
```scala
> fastOptJS/webpack
```
and is then to be found in the `scalajs-bundler` directory.   
In that case use `withPlugin.html`.

