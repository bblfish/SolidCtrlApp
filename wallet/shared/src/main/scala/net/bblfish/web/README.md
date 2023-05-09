## The Cache

We need a cache for web resources.

### Use ReactiveSolid's DirTree?

We could define a Cache for a given X and Y something like

```scala
type Cache = Map[(Protocol, Domain, port), DirTree[X, Y]]
```

Arguably the domain and subdomains could also be part of the dirtree,
if one wanted to think of domains hierarchically.

Some differences:

1. dirtree.insert removes subtrees when adding a ref for a container.
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
   but as explained [below](#finding-the-default-acl) we may not have done a `GET` onthe container of the effective acl,
   so when we work through a path hierarchy we would not have the info there to know that the container had a default
   acl. A good answer would be to allow one to add incoming links to the data structure.

## Implementations

There are a number of ways of modelling the file structure of a server which is essentially a Tree with data at the
nodes.

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
      Not really because if all we want is `/foo/bar/baz` we will need to load the Maps for `/foo` and `/foo/bar`, and
      since we are dealing with immutable data structures we have to load all the elements of the map at once. This
      actually shows the advantages of our tree of Actors, as those can only load one relation of the map when requested
      by checking the file system.

### Using `cats.effect.kernel.Ref`

In a strongly concurrent system with many threads and potentially a lot of writes, we may want to model
the Tree as an immutable structure
using <code>[cats.effect.kernel.Ref](https://typelevel.org/cats-effect/api/3.x/cats/effect/kernel/Ref.html)</code>.
That starts to be a lot closer to the original OO method of programming, as we no longer need a zipper to change
a node, but can just change the node itself, by for example creating a new `kids` in the parent, and replacing the
old one by updating the Ref.

```scala
case class DirTree[X](node: X, kids: Ref[Map[String, Tree[X]]])
```

On the client side, for the cache we don't really need to optimise for synchronous
access to the DB as this fetching data from a web server is restricted by the speed of
light and by the rules of politeness.

### Using actors as a Tree

That is actually what Akka does. Each actor has children in the form of a tree. The only reason we needed our
`DirTree` immutable data structure there was to more efficiently find references for those actors so that we did not
have to have 
* all messages pass through one root actor and 
* so we could reduce the message passing from paths of length n to 1

But the major advantage of actors is that they make it easy to check for files only when they are needed. So we don't
have to for example update the whole map by reading all the files present in one directory. We can just check that 
the directory or file we need is there. (see question 1 in [using Cofree](#using-catsfreecomonadfx) above)

## Finding the default ACL

The client needs a cache it can use to guess for as many resources as possible what the default acl for them could be.
without going through a `401` response. After all, if a client has fetched a resource in a container and later
wants to fetch other resources in that container or subcontainer then if the rule it used to authenticate was a default
rule then it makes sense that it could continue using that method of authentication.

For that to work, the client needs to keep a cache of the web, keeping information about the tree hierarchy structure of
the URL space.

To illustrate:

1. a client goes to `https://alice.name/blog/2023/04/01/party.html` and receives a `401` with
   a `Link: </blog/.acr>; rel=effectiveAccessControl` header.
2. it fetches the `</blog/.acr>` resource to learn how to interact with `party.html`.
3. it fetches `/blog/2019/12/12/promise.html` with the right signature successfully

If it later finds a link to `https://alice.name/blog/2019/12/12/promise.html`
then the client should try to immediately to sign on.

But that means that the client needs to know that `https://alice.name/blog/2019/12/12/promise.html` is a resource that
is also dependent on the same `https://alice.name/blog/.acr`. How does it know that? Well, it can't **know** that
without getting a `401` from `promise.html` above.
But it can argue that `promise.html` is part of the container `https://alice.name/blog/` and that since that containers'
access control resource is `/blog/.acr` the same default set of rules should apply. But we have not actually yet got the
piece of info that the access control rule of `/blog/` is `/blog/.acr`. At least not in the right place namely in
the cache position `/blog/`.

This could be solved in a number of ways.

1. the "effectiveAccessControl" Link could instead of pointing to the ACR, point to the container, which would require
   the client before 2. above to first do a `HEAD` on `/blog/` in order to find `/blog/.acr`
2. we could add to `/blog/.acr` a header `Link: <.>; rev="acl"; anchor=""`
3. the fact that `/blog/.acr` had a default rule on `/blog/` that enabled previous authentication should be good enough

Only 1 gives us direct evidence which would be visible from the cache.
We could nevertheless in the Data structure for `/blog/` make space for incoming links.
That would allow us to add information at `/blog/` about the acl for that resource.