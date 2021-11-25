## Launcher App

### Test in browser


1. To build the main app in sbt 
```scala
$ sbt
> project app
> fastOptJS/webpack
```

2. On the command line start a server in the [assets](assets) dir eg. with python
```
cd assets
python3 -m http.server --cgi 8080
```

3. Open a browser on http://localhost:8080/
and open the withPlugin.html page 

### Run tests

0. Install npm `npm install`?
1. in `sbt` run `test` in `app` project


### Todos: 
 
 * I am not sure why "jsdom": "^9.9.0" is installed and not 18.1.1 
 * We don't really need to `npm install buffer` I think, as we do not use it. It is probably used by N3 js package. But unsetting it with `buffer: false,` in  webpack.config.dev.js does not work.
