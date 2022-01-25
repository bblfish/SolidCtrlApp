Solid-Ctrl App
==============

The Solid-Ctrl App provides an interface for Solid JS Applications 
to authenticate to remote Solid resources. That is when an application
receives a 40x response from a resource with an 
`WWW-Authenticate: HttpSig` header as defined in
[HttpSig](https://github.com/solid/authentication-panel/blob/main/proposals/HttpSignature.md)
it can pass the response and the desired request to the library which will

1. fetch the remote Web Access Control (WAC) Document for that resource
2. find out from the WAC doc if it has the required keys to sign the message
3. if it has the key (and no policy stops it signing such a request for that JS client) it can sign the specific request

This library could be used directly by any App. 
But since not every App should have the trust of a user, there needs to
be a trusted Solid-Ctrl App that can create asymmetric key-pairs, publish
them to the user's POD and do the signing for the end user following
policies allowed by the user (expressible using the WAC ontology we believe)

The details initial demonstration of how this can work are detailed in the 
[Launcher App](https://github.com/bblfish/LauncherApp) project.

## Notes

This library currently contains a Scala-JS wrapper for a number of Turtle-like language
parsers, and these will need to be moved to banana-rdf at some point.

## Prerequisites

You should make sure that the following components are pre-installed on your machine:

 - [Node.js](https://nodejs.org/en/download/)
 - [Yarn](https://yarnpkg.com/en/docs/install)

## Create a module
in sbt shell: `fastOptJS::webpack` or `fullOptJS::webpack`

## Working in dev mode
In sbt shell, run `dev`. Then open `http://localhost:8080/` in your browser.

This sbt-task will start webpack dev server, compile your code each time it changes
and auto-reload the page.  
webpack dev server will close automatically when you stop the `dev` task
(e.g by hitting `Enter` in the sbt shell while you are in `dev` watch mode).

If you exited ungracefully and your webpack dev server is still open (check with `ps -aef | grep -v grep | grep webpack`),
you can close it by running `fastOptJS::stopWebpackDevServer` in sbt.

## Thanks

This work was made possible by the generous EU grant from nlnet for
the [Solid Control Project](https://nlnet.nl/project/SolidControl/).
That project is to end in January 2022.

If you wish this project to progress faster please contact [henry.story@co-operating.systems](mailto:henry.story@co-operating.systems) or leave
issues in the Issue DataBase.

We are looking for further funding opportunities to continue the work.