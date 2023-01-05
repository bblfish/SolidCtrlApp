## Architecture

### Actors

We need to build a client that can work as a proxy on the server or inside a [LauncherApp](https://github.com/bblfish/LauncherApp) in the browser. 
1. JS Apps Lauched by the LauncherApp will communicate with the wallet via `Windows.postMessage` to have headers signed, which the client can then use to make the request. 
2. The Launcher App could also make the calls directly to a POD proxy, as in 3. below
3. The App could make requests to the POD proxy which would then when valid sign the requests, allowing the proxy to check the returned content.

The point is we will need the logic to function in the browser or on the server, so ideally we'd like to not duplicate the code.

### Authentication methods

The server can signal its support for any of these via a [WWW-Authenticate](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/WWW-Authenticate) header. Suppose we allow the following types of authentication.
1. [Basic](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#basic_authentication_scheme): too simple not to implement;
2. [Digest](https://datatracker.ietf.org/doc/html/rfc7616): which should be the symmetric key authentication that a user uses for his own POD;
3. [HttpSig](https://github.com/bblfish/authentication-panel/blob/main/proposals/HttpSignature.md): the most efficient and secure authentication, best for connecting to other servers
4. UMA: as described in [Solid-OIDC](https://solidproject.org/TR/oidc-primer) is useful as a way of tying into widely deployed OAuth systems, but not efficient for highly decentralised apps.
5. Credential based - find the spec

We will get going with 1 and 3, but keeping 2 and 4 in mind will be helpful to make sure the architecture is correct. In any case the system should be extensible so that others can contribute other auth schemes without problem.

### Client Sequence Diagram 

For servers other than the home server we extend the basic authentication diagram [shown on the MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) to:
      
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
|                                                                    |
|<--------------------------------------------(4a) answer resource---|
```

We are only concerned about the client here:

2. the Client must follow a link to the ACL after receiving a 401 (if the header contains such a link) in order to find which of its credentials would be acceptable (eg. work, personal, age credential). This may require fetching more than one (potentially cached) documents. Eg. it could require fetching links to groups referenced by the client, or even follow hierarchies downwards. Note that even when a client knows the password for the server it is connecting to, it may want to know the access control rule, as that could tell it to use another less powerful certificate.
3. Having selected the valid credentials, and sorted them according to some preferences, the user can select one, or the wallet can follow some preferences expressed by that user previously.
4. add the needed headers to the next request to authenticate, signing it in one way or another, by adding a password, signature or token.

### Components

So now we can look at the components

Hea




 
