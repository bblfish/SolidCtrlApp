## The Cache

We need a cache for web resources that can be used by the Wallet.
Ideally it would also be a good tool for
implementing [RFC 911: HTTP Caching](https://httpwg.org/specs/rfc9111.html#caching.negotiated.responses).

## Use Cases

Apart from the very much needed use case of speeding up clients, reducing traffic
and perhaps even keeping an archive of what was found.

### Finding the default ACL

One major use case for our cache which is a bit unusual from other use cases, is that we
want a cache for
the wallet to find the defaul ACL.

The client needs a cache it can use to guess for as many resources as possible what the
default acl for them could be.
without going through a `401` response. After all, if a client has fetched a resource in a
container and later
wants to fetch other resources in that container or subcontainer then if the rule it used
to authenticate was a default
rule then it makes sense that it could continue using that method of authentication.

For that to work, the client needs to keep a cache of the web, keeping information about
the tree hierarchy structure of
the URL space.

To illustrate:

1. a client goes to `https://alice.name/blog/2023/04/01/party.html` and receives a `401`
   with
   a `Link: </blog/.acr>; rel=effectiveAccessControl` header.
2. it fetches the `</blog/.acr>` resource to learn how to interact with `party.html`.
3. it fetches `/blog/2019/12/12/promise.html` with the right signature successfully

If it later finds a link to `https://alice.name/blog/2019/12/12/promise.html`
then the client should try to immediately to sign on.

But that means that the client needs to know
that `https://alice.name/blog/2019/12/12/promise.html` is a resource that
is also dependent on the same `https://alice.name/blog/.acr`. How does it know that? Well,
it can't **know** that
without getting a `401` from `promise.html` above.
But it can argue that `promise.html` is part of the container `https://alice.name/blog/`
and that since that containers'
access control resource is `/blog/.acr` the same default set of rules should apply. But we
have not actually yet got the
piece of info that the access control rule of `/blog/` is `/blog/.acr`. At least not in
the right place namely in
the cache position `/blog/`.

This could be solved in a number of ways.

1. the "effectiveAccessControl" Link could instead of pointing to the ACR, point to the
   container, which would require
   the client before 2. above to first do a `HEAD` on `/blog/` in order to
   find `/blog/.acr`
2. we could add to `/blog/.acr` a header `Link: <.>; rev="acl"; anchor=""`
3. the fact that `/blog/.acr` had a default rule on `/blog/` that enabled previous
   authentication should be good enough

Only 1 gives us direct evidence which would be visible from the cache.
We could nevertheless in the Data structure for `/blog/` make space for incoming links.
That would allow us to add information at `/blog/` about the acl for that resource.

## Data Strutures

There are 2 ways of modelling a web cache

1. using a key-value store
2. keeping the Url tree hierarchy information

### The Key/Value store

The Key/Value store boils down to some simple structure such as

```scala
type KVCache[X] = Map[Uri, X]
```

with ways of updating the Map with new content.
If we want to find the default ACL for a resource we would need then to do multiple
requests
to the `KVCache` going down one directory at a time on the cache. So if the client
wanted to find the ACL for `https://alice.name/events/2023/04/01/birthday` it would have
to go to

1. `https://alice.name/events/2023/04/01/birthday`
2. `https://alice.name/events/2023/04/01/`
3. `https://alice.name/events/2023/04/`

Until one found some content with a header pointing to the default acl.

Notes:

1. We will probably have most requests return None.

### The Directory Tree

A directory path with content at the nodes is a recursive
data structure which we can call DirTree, which is approximately

```scala
case class DirTree[R](ref: R, kids: Map[String, DirTree[R]])
```

That structure allows us to represent directory trees. But
we want one for each web server. So we need to extend it with
the Authority information which we can represent by the
triple`(Protocol, Domain, Port)` so that we have:

```scala
type Cache[X] = Map[(Protocol, Domain, port), DirTree[X]]
```

There are quite a few variation on representing the DirTree as
we will see.

A `DirTree` for a cache is likely to have most nodes be unknown, so
that we will have `X = Option[Y]`.

Advantages:

* maps well to file system as a way of storing the data
* can find default acls as we walk up the hierarchy

### Comparison between Key/Value and DirTree?

At first sight one seems to be able to do the same in both.
But there are important differences.

#### KVCache is low resolution

With a KV datastore one could make a large number of requests to a server without
ever realising that one has no information at all about it. So if one had a url with 40
slashes, one may need to make 40 requests to the KV DB before concluding that one had no
information about the default acl.

With the Tree one would know immediately, just from the fact that one could
not find the server. If one had the server and a few directories, one would
find out by walking the tree hierarchy what the first container one knew about was.

So if one finds one knows nothing about `https://alice.name/events/` and we don't
have a default acl even for that then we can immediately
fetch `https://alice.name/events/2023/04/01/birthday` on the Web, it should return a 401
with a Link header.

#### Better mapping to directory structure

A DirTree view is going to be closer to the directory structure in which one can save
representations, which makes it easier to the find documents using normal unix commands.

That means that representations using relative URL could function on the
local file system correctly, allowing one to view files locally, as rendered.
(Note there will be subtle problems with naming)

It also means we could use the actor hierarchy for working with the caches too.

The advantage is also that as the search walks through the tree hierarchy it will find
the closest default ACL it knows about. The further it gets to the resource the more
precise it's knowledge of the default acl will become (if it has one).

## Using  Key-Value DBs

[Mules](https://github.com/davenverse/mules) is a Scala library that abstracts over
key-value pairs using the Tagless final pattern.
Here the DB is something simple like

```scala
type Cache[X] = Map[Uri, X]
```

### Abstracting in the manner of Mules

An http
implementation [mules-http4s](https://github.com/davenverse/mules-http4s/tree/main).
It uses the Tagless Final pattern to
define [Cache Traits](https://github.com/davenverse/mules/blob/main/modules/core/src/main/scala/io/chrisdavenport/mules/Cache.scala)
such as

```scala
trait Get[F[_], K, V] {
   def get(k: K): F[V]
}

trait Lookup[F[_], K, V] {
   def lookup(k: K): F[Option[V]]
}

trait Insert[F[_], K, V] {
   def insert(k: K, v: V): F[Unit]
}
```

which are then brought together in a Cache

```scala
trait Cache[F[_], K, V]
 extends Lookup[F, K, V] with Insert[F, K, V] with Delete[F, K]
```

Could we extend that with a basic `Search` trait?
We want to for a given URI find out if one of the subdirectories
contains [the default ACL](#finding-the-default-acl).

```scala
trait Search[F[_], K, V] {
   def find(start: K)(f: (K, V) => Either[K, Option[V]]): F[Option[V]]
}
```

Essentially this could run the function evaluated on the cache if possible to speed
things up. This function would start by fetching the original url, and pass the pair of
that url and the value to the function `f` and return either a new key to search on the
next iteration or the resulting value to end the calculation.

This would allow one to search Key/Value store and follow links without the store
organising its knowledge using the tree hierarchy. Though it could.

The `Search` trait could also be implemented externally using the previous Cache
functions.

#### Questions:

1. Could a `Cache` including the `Search` trait provide the abstraction needed to cover
   both KeyValue DBs like [Caffeine](https://github.com/ben-manes/caffeine) and `TreeDir`
   like databases?
2. Could such a `Cache` trait also be used to create Free Monads of Commands that could be
   sent to our actor based cache?

If 1 were true then 2 could be built out of the other methods without needing `Search`.

The answer to 2 is yes. This is clearly explained in the Smithy4S
documentation [The Duality of Final and Initial algebras](https://disneystreaming.github.io/smithy4s/docs/design/services/#the-duality-of-final-and-initial-algebras)
where they mean the duality between finally encoded and initially encoded algebras, ie
between Free Monads and finally Tagless. And even better here is that the example given is
of a `KVStore`!

Not only that but it suggests that we could use the Finally Tagless model for
HTTP requests, and then have an interpreter for the Resource, Container, ACL
etc, with implementations on how to write to file, create new dir, etc...

Then our natural transformation interpreter would just interpret the
incoming messages. We should be careful to return results in such a
way that the actor can from the result decide how to send it on.

## Implementations of DirTree

### Using DirTree from ReactiveSolid

In ReactiveSolid we implemented an
immutable [DirTree](https://github.com/co-operating-systems/Reactive-SoLiD/blob/30d6fd46fe30bc8be350c4a09d8592b20b791aa7/src/main/scala/run/cosy/ldp/DirTree.scala#L135).
Simplifiying just a little it is defined as:

```scala
case class DirTree[R](ref: R, kids: scala.collection.immutable.HashMap[String, DirTree[R]])
```

The library then implements its own zipper class to allow insertions and
changes to that immutable recursive data structure.

Because it is immutable:

* read access is easy
* write access will change the whole tree including the root.

If a client wants access to the latest version, we need to wrap the root in a
java `AtomicReference[DirTree[R, A]]` or a cats effect `Ref`

Arguably the domain and subdomains could also be part of the dirtree,
if one wanted to think of domains hierarchically.

Some differences:

1. dirtree.insert removes subtrees when adding an ActorRef for a container.
   But:
    * would placing the cached content in the attribute help?
    * could one just rename this as `insertAndRemoveSubDirs` and create a
      new method `replace` that did notchange subdirs?
2. Here we mostly do not need intermediate resources, where in akka those are
   mandatory as they need to keep track of properties of the collections.
   (e.g. a collection may have config info that tells the server to fetch data
   from a DB) But:
    * We could have `type X = Option[G]` instead of just `G`
    * We do need intermediary resources if we want to keep track of
      default ACLs, since we need to be able to search for resources at a domain
      by path, and those are in tree form

Improvements needed:

1. Metadata: the container points to its ACL,
   but as explained [below](#finding-the-default-acl) we may not have done a `GET` onthe
   container of the effective acl,
   so when we work through a path hierarchy we would not have the info there to know that
   the container had a default
   acl. A good answer would be to allow one to add incoming links to the data structure.

### Old OO style

In old OO style we could just use mutable objects.

```scala
case class DirTree[R](var ref: R, kids: scala.collection.mutable.HashMap[String, DirTree[R]])
```

changing something in the tree would just require

1. finding the node
2. setting the `ref` or `kids` var to the new value

That requires the whole tree to be synchronised for writes if paralle access is
possible as it is in Java.

### Using `cats.free.Comonad[F[_],X]`

The definition of `cats.free.Cofree` is

```scala
final case class Cofree[S[_], A](head: A, tail: Eval[S[Cofree[S, A]]])
```

If we fix `S[_]` to `Map[String,_]` as the branching Functor
and if we ignore `Eval` or just fix it to `Now`, we get an equivalent
definition as above.

```scala
type Dir[X] = Now[Map[String, X]]
type DirTree[N] = cats.free.Cofree[Dir[_], N]
```

Advantages:

* gives us all the builtin tools to work with the Directory,
* requires us to build a zipper to change the content. We could:
    + re-use the one from the Reactive-Solid lib of course
    + use Monocle
      as [in the Solid Server demo](https://github.com/bblfish/lens-play/blob/master/src/main/scala/server/SolidServer.scala#L181)
* allows the use of `tree.coflatMap(f: DirTree[N] => A)`. Would be useful for:
    * perhaps finding the ACLs linked to a directory
    * ???

Questions:

1. could the lazy evaluation be useful with `Eval.Later` or `Eval.Always`?
    * when loading data from the file system?
      Not really because if all we want is `/foo/bar/baz` we will need to load the Maps
      for `/foo` and `/foo/bar`, and
      since we are dealing with immutable data structures we have to load all the elements
      of the map at once. This
      actually shows the advantages of our tree of Actors, as those can only load one
      relation of the map when requested
      by checking the file system.

### Using `cats.effect.kernel.Ref`

In a strongly concurrent system with many threads and potentially a lot of writes, we may
want to model
the Tree as an immutable structure
using <code>[cats.effect.kernel.Ref](https://typelevel.org/cats-effect/api/3.x/cats/effect/kernel/Ref.html)</code>.
That starts to be a lot closer to the original OO method of programming, as we no longer
need a zipper to change
a node, but can just change the node itself, by for example creating a new `kids` in the
parent, and replacing the
old one by updating the Ref.

```scala
case class DirTree[X](node: X, kids: Ref[Map[String, Tree[X]]])
```

On the client side, for the cache we don't really need to optimise for synchronous
access to the DB as this fetching data from a web server is restricted by the speed of
light and by the rules of politeness.

### Using actors as a Tree

That is actually what Akka does. Each actor has children in the form of a tree. The only
reason we needed our
`DirTree` immutable data structure in Reactive-Solid was to more efficiently find
references for those actors so that we
did not
have to have

* all messages pass through one root actor and
* so we could reduce the message passing from paths of length n to 1

But the major advantage of actors is that they make it easy to check for files only when
they are needed. So we don't
have to for example update the whole map by reading all the files present in one
directory. We can just check that
the directory or file we need is there. (see question 1
in [using Cofree](#using-catsfreecomonadfx) above).

Note: The
gist [Typed Actors using Cats Effect, FS2 and Deferred Magic](https://gist.github.com/Swoorup/1ac9b69e0c0f1c0925d1397a94b0a762)
could be useful.

## Conclusions

The complications of implementations can be hidden behind a final tagless interface as is
done with [Mules](https://github.com/davenverse/mules). This allows us to switch
between implementations as suite the environment, yet write the Wallet
in a simple way.

* Can one use Free Monads with actor interpretaers for our cache?
    - yes
* Can one use the `Finally Tagless` method to abstract a Free Monad encoding that
  would also work with sending messages to actors using say a much simpler interpreter
    - yes. Finally tagless can be converted to Free Monads without loss


