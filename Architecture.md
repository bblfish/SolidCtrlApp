## Architecture

### Actors

We need to build a client that can work as a proxy on the server or inside a [LauncherApp](https://github.com/bblfish/LauncherApp) in the browser. 
1. JS Apps Launched by the LauncherApp will communicate with the wallet via `Windows.postMessage` to have headers signed, which the client can then use to make the request. 
2. The Launcher App could 
   - sign the requests itself 
   - or make requests to a Personal Online Data Store (POD) proxy which could sign the requests before sending them on  

The above shows that we will need authentication logic to function in the browser or on the server, so ideally we'd like to not duplicate the code.

### Authentication methods

The server can signal its support for any of these via a [WWW-Authenticate](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/WWW-Authenticate) header. Suppose we allow the following types of authentication.
1. [Basic](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#basic_authentication_scheme): too simple not to implement;
2. [Digest](https://datatracker.ietf.org/doc/html/rfc7616): which should be the symmetric key authentication that a user uses for his own POD;
3. [HttpSig](https://github.com/bblfish/authentication-panel/blob/main/proposals/HttpSignature.md): the most efficient and secure authentication, best for connecting to other servers
4. UMA: as described in [Solid-OIDC](https://solidproject.org/TR/oidc-primer) is useful as a way of tying into widely deployed OAuth systems, but not efficient for highly decentralised apps.
5. Credential based - find the spec
6. Cookies: once a user is authenticated for a realm, using a cookie may be enough to continue the interaction.

We will get going with 1 and 3, but keeping 2 and 4 in mind will be helpful to make sure the architecture is correct. In any case the system should be extensible so that others can contribute other auth schemes without problem.

### Client Sequence Diagram 

When the client is accessing servers other than the user's POD, we extend the basic authentication diagram [shown on the Mozilla Developer Network](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) with an extra potential request to the Web Access Control (WAC) Document in 2, so that the client can find out which credential it could use, before authenticating:
      
```
Client                                            WAC           Resource
App                                             Document         Server
|                                                   |                |
|-(1) request URL -------------------------------------------------->|
|<-----------(1a) 40x + WWW-Authenticate: HttpSig + (Link) to ACR  --|
|                                                   |                |
|                                                   |                |
|-(2) GET WAC ------------------------------------->|                |
|<-------------------(2a) 200 with rules -----------|                |
|                                                                    |
| (3) (select identity)                                              |
|                                                                    |
|-(4)- Add Authorization header or signature to new request -------->|
|                                                                    |
|                                                                auth|
|                                                        verification|
|                                                                    |
|<--------------------------------------------(4a) answer resource---|
```

We are only concerned about the client here, so we don't show what the
server needs to do to verify the signed request in (4). Here are the main 
stages of the Client's behavior:

1. The client makes a request which returns a 401 (1a) with a link the Web Access Control (WAC) document.
2. it fetches the WAC doc to find out if any of the credentials it holds would be acceptable (eg. work or personal ID, age credential, ...). This may require fetching more than one (potentially cached) documents. Eg. it could require fetching links to groups referenced by the client, or even follow hierarchies downwards. Note that even when a client knows the password for the server it is connecting to, it may want to know the access control rule, as that could tell it to use another less powerful certificate.
3. The client having selected the valid credentials and sorted them according to some preferences, the user can select one, or the wallet can follow some preferences expressed by that user previously.
4. the client then adds the needed headers to the next request, making it an authenticated request by signing it in one way or another (eg. by adding a password, signature or token)
 
## Client Middleware

In http4s [a Client is defined](https://http4s.org/v1/docs/client.html) as a function from a `Request[F]` to a `Response[F]`, wrapped in a Resource type to take care of opening and closing of connections. 

```scala
trait Client[F[_]]:
  def run(req: Request[F]): Resource[F, Response[F]]
  //...
```

This parallels the way Http4s defines server applications. As Ross Baker quipped in his [introductory talk]() ([video](https://www.youtube.com/watch?v=urdtmx4h5LE)):
    
> HTTP applications are just a Kleisli function from a streaming request to a polymorphic effect of a streaming response. So what's the problem?

This is just a little step beyond the definition
```scala
type HttpApp = Request[F] => F[Response[F]]
```

Where `F` is the effect of the stream and the response, 
usually `IO` or `Task`.

The difference between a server App and a client is that the client has to open a socket to the remote server (or find a cached connection) whereas for the server app, that has already been taken care of. Hence the client needs the `Resource` type.

What we are constructing here, is what http4s calls a [Client Middleware](https://http4s.org/v1/docs/client.html#middleware). That is
a function that takes a Client and some extra data and returns a new Client. 

A relevant example of a Middleware is the [CookieJar](https://github.com/http4s/http4s/blob/main/client/shared/src/main/scala/org/http4s/client/middleware/CookieJar.scala) which saves cookies to its DB and adds cookies to requests if available. Note that the rules for what cross origin JS can see of cookies is complex (see [video](https://www.youtube.com/watch?v=34wC1C61lg0). It [cannot see Set-Cookie headers sent by the server for example](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie). Q: could they still be  useful for a server to reduce the need for authentication?)

The extra data we need is what we call a `Wallet`

```scala
trait Wallet[F[_]]:
  def authn(failed: Response, lastReq: Request): F[Request]
```

The wallet can take a failed response (some 40x one) and the last Request - avoiding redirects - and by analysing the response for links to ACL headers, fetching those, find matching credentials in the Wallet, and after asking the user or his policies, authenticating the request by adding the needed headers to the request. 

The Client Middleware thus takes a client - one that can correctly follow redirects perhaps - and transforms it into a new client that `run`s a request, and if the returned `Response` is 40x-ed can build an authenticated request using `Wallet.authn` and then run that.

The AuthN Client MiddleWare will thus have a signature

```scala
def AuthNClient[F[_]](wallet: Wallet[F])(client: Client[F]): Client[F]
```

## Components of the Wallet

So now we can look at the components needed to build a Wallet.
Ideally we would like this to be extensible so that if new authentication schemes come along it would be easy to fit them in.


### AuthN Functions

Once the Wallet knows what Identity it should use, it can sign
the request. All the Wallet needs to know when it signs a request
is the following interface

```scala
trait AuthN:
   def sign(originalReq: Request): Request
```

Different authentication methods will need different types of 
data to construct their `AuthN` function.

So for example Signing with a Password for a domain usually only requires the request, from which the Authority and protocol can be extracted, allowing one to return the request with the appropriate `Authorize: Basic ...` header appended.

```scala
class Basic(id: UsernamePass) extends AuthN: 
  def sign(originalReq: Request): Request = ???
```

A function implementing HttpSig, will need to extract some data from the 40x Response object to check if it comes with an [Accept-Signature](https://www.ietf.org/archive/id/draft-ietf-httpbis-message-signatures-15.html#name-requesting-signatures) header that would tell the client what headers it needs to sign. There may be other data needed too later such as a proof hint as to how that was determined.

```scala
class HttpSig(
   keyId: KeyIdData,
   acceptSig: Option[Header], 
   proof: Option[Proof]
):
   def sign(originalReq: Request): Request = ???
```
      
We can think of similar functions for the other authentication protocols including Cookies.

### Selecting the right credential

Before getting to the point of signing a Request, we need to 
find out which credentials could be used. (We are using credentials here
in a very general way, where a password or a public key counts as a credential)

We will need 
1. a function to fetch and interpret the WAC document into an RDF Named Graph, and the ability to follow links to other NGraphs.
2. a function to search the for valid credentials. Starting from the WAC Named Graph, following links to other resources, and data in the Wallet DB, this should return an asynchronous Sequence of Credentials, with potentially proofs of how they fit the rules. Each credential may require searching the data differently.     

#### 1. Fetch WAC

```scala
def fetchWAC[F[_]](response: Response): F[Option[NGraph[Rdf]]]
```

This has to 
1. find a `Link: <doc.wac>, rel=acl` header in the Response
2. fetch the WAC document, in the cache or on the web - hence the need for the effect type `F`

#### 2. Find relevant rules

Once one has Web Access Control Document, one can find the rules
that apply to the requested resource for the given mode. 

```scala
def findValidRules(wac: NamedGraph, re: URL, mode: Mode): Seq[PNG] = ???
```

This will return a Seq of `PointedNamedGraphs` which we will abbreviate with `PNG` which is a triple of a NamedGraph and a pointer onto a node in the graph. The pointer should be the node of a `wac:Authorization` where

```turtle
@prefix wac: <http://www.w3.org/ns/auth/acl#>
```

From here on we just need ways to test if that node gives access
to the user under specific types of descriptions.

#### 3. Prooving access

Starting from a particular PNG on a `wac:Authorization` we
return a collection of proofs.
                   
```scala
trait IDVerifier:
   type ID <: AgentID
   def verify[F[_]](id: ID, rule: PNG): F[Seq[Proof]]
```

The result is wrapped in an effect Monad, as the verification will very often consist in Web or WebCache requests.

Different types of IDs will have different methods of providing
a proof, so we expect to have subclasses of `IDVerifier` such as

```scala
class KeyIDVerifier extends IDVerifier:
   type ID = KeyID
   def verify[F[_]](id: KeyID, rule: PNG): F[Seq[Proof]] = ???
```

Other subclasses would be `SolidOIDCVerifier`, or WebIDVerifier. 
WebIDVerifier could be composed of others such as `SolidOIDCVerifier` or `KeyIDVerifier` or even verification via e-mail, since a WebID could have a description such as 

```turtle
<#i> a foaf:Person;
   foaf:mbox <mailto:henry.story@bblfish.net>;
   foaf:openid <https://openid.github.com/> ; # find correct url
   cert:key </keys#k1> ;
   solid:oidcIssuer <https://secureauth.example> .
```

Todo: define Proof. We may start with Proof of type `1`, so that we have either an empty sequence of a non-empty sequence, giving us the equivalent of a Boolean.

### Selecting the Id appropriate for the allowed Authenticate methods

This may be done earlier too, filtering out early Ids that won't be
useful. A 401 response can come back with 0 or more [WWW-Authenticate](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/WWW-Authenticate) methods. Not all IDs compatible with the WAC file will be useable with the returned set of `WWW-Authenticate` methods. Eg. At this point, a KeyId is not useable for Solid-OIDC and vice-versa.

Once the correct ID select, the appropriate `AuthN` type can be used to sign the Request.









              





 
