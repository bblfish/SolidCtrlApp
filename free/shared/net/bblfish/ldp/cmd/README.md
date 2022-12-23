# Free Monads and Applicatives using LDP Commands

LDP Commands for a free monad and free applicatives.
(see "An Intuitive Guide to Combining Free Monads and Free Applicatives"
  https://twitter.com/bblfish/status/1587052879986180097 )

## Previous Work

Note this has been implemented before:  
 * 2012 - [rww-play LDPCommand](https://github.com/read-write-web/rww-play/blob/dev/app/rww/ldp/LDPCommand.scala): this implementation was the first ldp implementation with commands interpreted by actors, allowing multiple implementations of the same command depending on the actor. The structure here was too strict though because it only allowed for RDF Graphs to be fetched, POSTed, PUT and Queried. But a Solid Web server can contain any type of data: html, images, films, and many other data formats. One expects all the important metadata to be in RDF, but following links like `foaf:img` can always lead one to a referenced literal type.
 * 2021 - [Reactive Solid: SolidCmd](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/ldp/SolidCmd.scala): this implementation was less uniform as it also made space for plain http requests and responses. An Akka `HttpResponse` contains a `ResponseEntity` that wraps a `ByteString`. It also looked into using CoMonads to capture the result of following links through pages (in that case an `owl:imports` link) 

In both those cases the advantage of the Free Monad as opposed to Final Tagless, is that being pure data, we can interpret each command as they are unfolded in a script in a different way. Each Command comes with a URL and each actor at that URL can interpret the command differently. The actor can then pass the result on to the next part of the script which can be in its turn interpreted by a new actor. We see this unwrapping
of scripts in
* in rww-play [LDPRActor.run[A]](https://github.com/read-write-web/rww-play/blob/dev/app/rww/ldp/actor/local/LDPRActor.scala#L315) resumes a script and either sends a response back or send the new Cmd to be interpreted locally in [runLocalCmd(cmd: LDPCommand[Rdf, LDPCommand.Script[Rdf, A]])](https://github.com/read-write-web/rww-play/blob/dev/app/rww/ldp/actor/local/LDPRActor.scala#L179)
* in Reactive-Solid after routing to the right actor e.g a [Resource](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/ldp/fs/Resource.scala#L704), that resource interprets the top message and then calls [CmdMessage.continue](https://github.com/co-operating-systems/Reactive-SoLiD/blob/d5352e163d84dc1159250b33c56db262e2d2a76f/src/main/scala/run/cosy/ldp/Messages.scala#L52) to send the request on to the next actor for evalution.

## Limitations of previous work

Because of the use of free monads, both of those allow one to write scripts using for loops that can fetch data from one resource, follow links, jump to another resource, follow links, ... But 

 * the web is a space of processes happening in parallel. One can follow a `foaf:knows` relation and end up on resources spread across the world. There is no reason one should have to follow those links linearily - one after another. Indeed we want to follow the links in parallel if possible. One way to do this would be to provide not just Free Monads but also Free Applicatives as explained in "[An Intuitive Guide to Combining Free Monads and Free Applicatives](https://twitter.com/bblfish/status/1587052879986180097)"
 * We need to provide more clearly for the fact that a response can either be an RDF data structure or some literal such as a video, img, etc... The web cannot be ordered to be just a pure category of rdf graphs, as it is a decentralised medium. So this fact has to be accepted and embraced. We want to keep the ability to get Graphs back (because a cache may already have parsed the locally) and it would be a waste to serialised and deserialise them.
 * We need to make sure that we can have interpreters that can do authentication for us as well as read access control rules (and other things such as follow redirects). This seems to be very consistent with the architecture we have, but the previous access-control implementations were from the server perspective, not the client one.

We also want to explore further the use of comonads. For example an [LDES](https://semiceu.github.io/LinkedDataEventStreams/) use case could be to follow links to fetch all the new resources of the Linked Data Event Stream. There we are interested less in arriving at a node, as we are in collecting a path of resources.

## Current Implementation

Some questions that should be thought about. Answers will come later.

### Following the reasoning in the Guide

We can follow the reasoning from the [Guide to Combining Free Monads and Apps](https://twitter.com/bblfish/status/1587062094775525376) by starting with modeling our interface as a trait
The simplest model for LDP used in rww-play is 

```scala
trait LDPCmd:
  def getGraph(url: Uri): Graph[Rdf]
```

But we cannot guarantee that a graph will be returned by the server.
(unless we restrict the mime types in the request?) Should
we therefore model it as:

```scala
trait LDPCmd:
  def getGraph(url: Uri): Either[http4s.Response,Graph[Rdf]]
```

Where we either receive an RDF Graph as a result or if the server returns an error or a non-rdf response we return an Http `Response`. 

Note that the request itself may fail so that we don't even succeed in reaching the server - which may not even exist. So we should capture those
types of  errors in a Try.

```scala
trait LDPCmd:
  def getGraph(url: Uri): Try[Either[http4s.Response,Graph[Rdf]]]
```

May we perhaps not want sometimes the returned graph to come with header information, for following links to metadata? 

```scala
trait LDPCmd:
  def getGraph(url: Uri): Try[Either[http4s.Response,(htt4s.Headers, Graph[Rdf])]]
```

But do we not sometimes want the whole history of request and responses the lead up to a response to deal with redirects? Lets not go down that road now...

We certainly will want an Asynchronous wrapper, because requests are
effectful and take a lot of time.

```scala
trait LDPCmd:
  def getGraph(url: Uri): IO[Try[Either[http4s.Response,(htt4s.Headers, Graph[Rdf])]]]
```

But, do we want to be tied to the `IO` Monad or should we use `Task`? These
problems become insurmountable. There is no good answer it seems. We are, to repeat slide 12 of the Guide
 * tightly coupling implementation details to domain  
 * cannot test domain logic in isolation of implementation
   
The Final Tagless answer is to abstract the effects in a Monad F

```scala
trait LDPCmd[F[_]]:
  def getGraph(url: Uri): F[Graph[Rdf]]
```

Our question would be: do we want to capture all the context in one `F`, or would it be useful to leak some of that information (perhaps the Http Response?). On slide [on slide 11](https://www.slideshare.net/CameronJoannidis/an-intuitive-guide-to-combining-free-monad-and-free-applicative) of Cameron Joannidis' talk on combining Monads and Applicatives suggests that all the context up to `User` (`Graph[Rdf]` in our case) could be considered as part of the Context. 
But that may be going too far. 

The type inside the `F[_]` is all that code working with the script can use. 
If we place the type `Graph[Rdf]` there then we are building an algebra
that is forced to ignore network exceptions or non existent resources. 
But what happens on failure then?
Well if we work with Monads as sequential systems then the failure of a whole
monad would of course not allow the next step to take place. 

But in our case we have a lot of paralellism. 
Following a `foaf:knows` relation leads to a Sequence of `PointedGraphs` each of which we may want to follow by a `foaf:name` relation. 
The second relation can only be followed on the successful first ones. 

### Rigidity problem of the Applicative Inside the Monad

Have we just stumbled on something too rigid in this App inside Monad model?
Say we model our free monad with the following algebra

```scala
case class GetPG[A, R <:RDF](
        url: Uri, 
        k: PointedNamedGraph[R] => A
) extends LdpCmd[A]
```
The we could perhaps build a Script that goes like this

```scala
for
  pg <- getPNG("https://bblfish.net/people/henry/card#me").seq  //1
  pgs <- (pg /~ foaf.knows).par  //2. jump to remote graphs if needed
  pg2 <- (pg /~ foaf.knows).par  //3. 
yield pg2
```

Here clearly we cannot get to the second line before we get the initial card.
But do we need to wait of all the foaf.knows objects to have been found before
moving on to the second `foaf.knows` relation on line 3? 

Indeed if we run [the example code](https://github.com/bblfish/FreeMonadAndApplicative/blob/main/FreeApWithScalaz.scala) from the talk, the second round has two parallel calls following one another, and it is clear that the second parallel one must wait on the first. But that is because the second one depends monadically on the first. Perhaps in our case this 
need not be so, so that we can avoid it? What we really want if for each new PNG that we reach we immediately follow the next relation without waiting on the rest. So we want a stream of results... 

Todo: consider the use of Cofree Comonads in [SolidCmd](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/ldp/SolidCmd.scala).



         
 