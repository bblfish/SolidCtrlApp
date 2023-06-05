/*
 * Copyright 2021 bblfish.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bblfish.wallet

import bobcats.*
import cats.data.NonEmptyList
import cats.effect.{Clock, Concurrent, IO}
import cats.implicits.*
import io.lemonlabs.uri as ll
import io.lemonlabs.uri.Url
import net.bblfish.app.Wallet
import net.bblfish.web.util.SecurityPrefix
import org.http4s as h4s
import org.http4s.client.Client
import org.http4s.headers.{Authorization, Link, LinkValue}
import org.http4s.{headers as h4hdr, Challenge, Request, Uri}
import org.typelevel.ci.CIString
import org.w3.banana.RDF.Statement as St
import org.w3.banana.io.{JsonLd, RDFReader, Turtle}
import org.w3.banana.prefix.WebACL.apply
import org.w3.banana.prefix.{Cert, WebACL}
import org.w3.banana.{prefix, Ops, RDF}
import run.cosy.http.Http
import run.cosy.http.auth.MessageSignature
import run.cosy.http.headers.Rfc8941.{IList, SfInt, SfString}
import run.cosy.http.headers.SigIn.KeyId
import run.cosy.http.headers.{ReqSigInput, Rfc8941, SigInput}
import run.cosy.http.messages.{NoServerContext, ReqSelectors}
import run.cosy.http4s.Http4sTp.HT as H4
import run.cosy.http4s.messages.SelectorFnsH4
import run.cosy.ld.http4s.RDFDecoders
import run.cosy.web.util.UrlUtil.{http4sUrlToLLUrl, llUrltoHttp4s}
import scodec.bits.ByteVector

import scala.concurrent.duration.FiniteDuration
import scala.reflect.TypeTest
import scala.util.{Failure, Try}
import io.chrisdavenport.mules.http4s.CacheItem
import run.cosy.http.cache.TreeDirCache
import run.cosy.http.cache.InterpretedCacheMiddleware.InterpClient
import org.http4s.HttpApp
import cats.effect.kernel.Resource
import run.cosy.http.cache.InterpretedCacheMiddleware
import io.chrisdavenport.mules.http4s.CachedResponse
import cats.MonadThrow
import org.http4s.EntityDecoder
import net.bblfish.wallet.BasicWallet.defaultAC
import org.w3.banana.prefix.LDP
import run.cosy.web.util.UrlUtil.*
import io.lemonlabs.uri.AbsoluteUrl
import io.lemonlabs.uri.config.UriConfig
import BasicWallet.*
import org.http4s.Method
import org.http4s.Method.{GET, HEAD}
import org.http4s.dsl.request

class BasicId(val username: String, val password: String)

/* The keyId points to the public key.
 * The private key is obtained elsewhere, and could also have a URL.
 * todo: the Public Key should be linked to the algorithm string
 *    so that it can be added when needed as `alg="..."` to the
 *    signature. (It should also be found in the remote jwt)
 * */
class KeyData[F[_]](
    val keyIdAtt: KeyId,
    val signer: F[ByteVector => F[ByteVector]]
):
//  import ops.{given, *}
//  lazy val keyIdAtt = KeyId(Rfc8941.SfString(keyId.value))

   def mkSigInput(now: FiniteDuration): ReqSigInput[H4] =
      import run.cosy.http.headers.SigIn.*
      ReqSigInput[H4]()(Created(now.toSeconds), keyIdAtt)

end KeyData

trait ChallengeResponse:
   // the challenge scheme for which this response is designed
   def forChallengeScheme: String

   //
   def respondTo[F[_]](
       remote: ll.AbsoluteUrl, // request for original Host
       originalRequest: h4s.Request[F],
       response: h4s.Response[F]
   ): F[h4s.Request[F]]

object BasicWallet:
   val defaultAC = "defaultAccessContainer"
   val defaultACOpt = Some(defaultAC)
   val ldpContains = "http://www.w3.org/ns/ldp#contains"

   /** The type of relations that can be found in the Link header, Left is reverse and Right is
     * forward, and the link is to the absoluteUrl from the document
     */
   type Rel = (Either[String, String], ll.AbsoluteUrl)

   /** extract the links from the headers as pairs of Eithers for rev or rel relations and the url
     * value, and absolutize the Url. Todo: we use ll.Uri here, because they have a clear type for
     * absolute urls, but this is a we really need a url abstraction that does this right
     */
   def extractLinks(
       requestUrl: AbsoluteUrl,
       reponseHeaders: org.http4s.Headers
   ): Seq[Rel] = reponseHeaders.get[Link] match
    case None => Seq()
    case Some(link) => link.values.toList.toSeq.collect {
        case LinkValue(uri, rel, rev, _, _) if rel.isDefined || rev.isDefined =>
          Seq(
            rel.toSeq.flatMap(_.split(" ").toSeq.map(_.asRight[String] -> uri)),
            rev.toSeq.flatMap(_.split(" ").toSeq.map(_.asLeft[String] -> uri))
          ).flatten
      }.flatten.map((e, u) => (e, u.toLL.resolve(requestUrl, true).toAbsoluteUrl))

   /** valuees for sorting priority of link relations
     *
     * + todo: if we knoew that the resource was a solid:Resource or a solid:Cointainer we could
     * also proceed looking at the Url structure to see if we find the acl + todo: if we follow
     * links, we need to make sure we don't follow the same link twice or go in circles
     */
   def relPriority(rel: Either[String, String]): Int = rel match
    case Left(`ldpContains`) => 3
    case Right("acl") => 2
    case Right(`defaultAC`) => 1
    case _ => 4

end BasicWallet

/** place code that only needs RDF and ops here. */
class WalletTools[Rdf <: RDF](using ops: Ops[Rdf]):
   import run.cosy.web.util.UrlUtil.*
   import ops.{*, given}

   val wac: WebACL[Rdf] = WebACL[Rdf]
   val foaf = prefix.FOAF[Rdf]
   val sec: SecurityPrefix[Rdf] = SecurityPrefix[Rdf]

   /** Given the signature of InterpretedCacheMiddleWare we are forced to store an rGraph in the
     * cache. This is not ideal, as it means that every request on the cache will need to transform
     * the rGraph into a Graph. To avoid this the client function would need to take a (response,
     * uri) pair as second argument.
     *
     * todo: is the final URL that a redirect goes to the URL to use to absolutize a graph, or is
     * the request URL after going through the redirects? Because that is important to know how much
     * info is needed to be able to interpret the relative graph.
     *
     * Todo: also store graphs for other responses? (e.g. error responses?)
     */
   def cachedRelGraphMiddleware[F[_]: Concurrent: Clock](
       cache: TreeDirCache[F, CacheItem[RDF.rGraph[Rdf]]]
   )(using
       rdfDecoders: RDFDecoders[F, Rdf]
   ): Client[F] => InterpClient[F, Resource[F, *], RDF.rGraph[Rdf]] = InterpretedCacheMiddleware
     .client[F, RDF.rGraph[Rdf]](
       cache,
       (response: h4s.Response[F]) =>
         if !response.status.isSuccess
         then
            Concurrent[F].pure(
              CachedResponse[RDF.rGraph[Rdf]](
                response.status,
                response.httpVersion,
                response.headers,
                None
              )
            )
         else
            import rdfDecoders.allrdf
            response.as[RDF.rGraph[Rdf]].map { rG =>
              CachedResponse[RDF.rGraph[Rdf]](
                response.status,
                response.httpVersion,
                response.headers,
                Some(rG)
              )
            }
       ,
       enhance = _.withHeaders(rdfDecoders.allRdfAccept)
     )

   /** The requesturi is within the container */
   def withinTry(requestUri: RDF.URI[Rdf], container: RDF.URI[Rdf]): Try[Boolean] =
     for
        case requ: ll.AbsoluteUrl <- requestUri.toLL
        case ctnrU: ll.AbsoluteUrl <- container.toLL
     yield
        val se = requ.schemeOption == ctnrU.schemeOption
        val ae = requ.authorityOption == ctnrU.authorityOption
        val rp = requ.path.parts
        // because ll.Url interprets the https://foo.bar/ as having a path with one empty string
        val cp = ctnrU.path.parts
        val cpClean = if cp.last == "" then cp.dropRight(1) else cp
        val pe = rp.startsWith(cpClean)
        se && ae && pe

   extension (uri: RDF.URI[Rdf])
     def contains(longer: RDF.URI[Rdf]): Boolean = withinTry(longer, uri).getOrElse(false)

   def findAclsFor(g: RDF.Graph[Rdf], requestUri: RDF.URI[Rdf]): Iterator[St.Subject[Rdf]] =
      import run.cosy.web.util.UrlUtil.*
      val directRules: Iterator[St.Subject[Rdf]] = g.find(`*`, wac.accessTo, requestUri).map(_.subj)
      val defaultRules: Iterator[St.Subject[Rdf]] = g.find(`*`, wac.default, `*`).collect {
        case ops.Triple(rule, _, defaultContainer: RDF.URI[Rdf])
            if defaultContainer.contains(requestUri) => rule
      }
      directRules ++ defaultRules

   def modeForMethod(method: h4s.Method): RDF.URI[Rdf] =
      import h4s.Method.{GET, HEAD, SEARCH}
      if List(GET, HEAD, SEARCH).contains(method) then wac.Read
      else wac.Write

   def findAgents(
       aclGr: RDF.Graph[Rdf],
       reqUrl: RDF.URI[Rdf],
       mode: h4s.Method
   ): Iterator[St.Object[Rdf]] =
     for
        ruleNode <- findAclsFor(aclGr, reqUrl)
        if !aclGr.find(ruleNode, wac.mode, modeForMethod(mode)).isEmpty
        obj <- aclGr.find(ruleNode, wac.agent, `*`).map(_.obj).collect[St.Subject[Rdf]] {
          case u: RDF.URI[Rdf] => u
          case b: RDF.BNode[Rdf] => b
          // todo: the key could be in a literal too !?
        }
     yield obj
end WalletTools

/** First attempt at a Wallet, just to get things going. The wallet must be given collections of
  * passwords for domains, KeyIDs for http signatures, cookies (!), OpenId info, ...
  *
  * Note that CookieJar is a algebra for a mutable used to produce a middleware. So perhaps a wallet
  * takes a set of middlewares each adapted for a particular situation, and it would proceed as
  * follows:
  *
  * it needs a client to follow links to the WAC rules, though it may be better if instead it were
  * given a DataSet proxied to the web to allow caching. On the other hand with free monads one
  * could have those be interpreted according to context... Todo: compare this way of working with
  * free-monads.
  *
  * @param db
  *   username/passwords per domain
  * @param keyIdDB
  *   Key Database
  * @param iClient
  *   this is an interpreted client that can fetch acls on the web and returns graphs. This allows
  *   us to pass in a cached interpreted client for example.
  */
class BasicWallet[F[_], Rdf <: RDF](
    db: Map[ll.Authority, BasicId],
    keyIdDB: Seq[KeyData[F]] = Seq()
)(iClient: InterpClient[F, Resource[F, *], RDF.rGraph[Rdf]])(using
    ops: Ops[Rdf],
    fc: Concurrent[F],
    clock: Clock[F]
) extends Wallet[F]:
   val wt: WalletTools[Rdf] = new WalletTools[Rdf]

   val reqSel: ReqSelectors[H4] = new ReqSelectors[H4](using new SelectorFnsH4())
   import ops.{*, given}
   import reqSel.*
   import reqSel.RequestHd.*
   import run.cosy.http4s.Http4sTp
   import run.cosy.http4s.Http4sTp.{*, given}

   import scala.language.implicitConversions
   import wt.*

   def reqToH4Req(h4req: h4s.Request[F]): Http.Request[H4] = h4req.asInstanceOf[Http.Request[H4]]

   def h4ReqToHttpReq(h4req: Http.Request[H4]): h4s.Request[F] = h4req.asInstanceOf[h4s.Request[F]]

   // todo: here we assume the request Uri usually has the Host. Need to verify, or pass the full Url
   def basicChallenge(
       host: ll.Authority, // request for original Host
       originalRequest: h4s.Request[F],
       nel: NonEmptyList[h4s.Challenge]
   ): Try[h4s.Request[F]] =
      // todo: can one use some of the info in the Challenge?
      val either =
        for
           _ <- nel.find(_.scheme == "Basic")
             .toRight(Exception("server does not make basic auth available"))
           id <- db.get(host).toRight(Exception("no passwords for hot " + host))
        yield originalRequest.withHeaders(
          Authorization(h4s.BasicCredentials(id.username, id.password))
        )
      either.toTry
   end basicChallenge

   // todo: do we need the return to be a Resource? Would F[Graph] be enough?
   /** Fetches the ACL for the given uri, and if it is not found, tries to find the ACL for the
     * linkedToContainer (not implemented yet).
     */
   def fetchAclGr(
       uri: AbsoluteUrl,
       onlyCache: Boolean = false
   ): Resource[F, RDF.Graph[Rdf]] = fetchRdf(uri, GET, onlyCache).flatMap { crG =>
     Resource.liftK(
       if crG.status.isSuccess && crG.body.isDefined
       then fc.pure(crG.body.get.resolveAgainst(uri))
       else fc.raiseError(Exception(s"Could not find ACL at $uri"))
     )
   }

   def findAclForContainer(
       containerUrl: AbsoluteUrl,
       onlyCache: Boolean = false
   ): Resource[F, RDF.Graph[Rdf]] = fetchRdf(containerUrl, HEAD, onlyCache).flatMap { crG =>
     // todo: potentially check for the right status codes...
     // which ones would be correct?  Clearly 201, 202, 404. But what others?
     extractLinks(containerUrl, crG.headers)
       .collectFirst { case (Right("acl"), uri: ll.AbsoluteUrl) =>
         fetchAclGr(uri.resolve(containerUrl).toAbsoluteUrl, onlyCache)
       }.getOrElse {
         Resource.raiseError[F, RDF.Graph[Rdf], Throwable](
           Throwable(s"Could not find ACL for $containerUrl")
         )
       }
   }

   /** fetch rdf graph at Url using Method, adding an `only-if-cached` header if needed. */
   def fetchRdf(
       url: ll.AbsoluteUrl,
       method: h4s.Method = GET,
       onlyCache: Boolean = false
   ): Resource[F, CachedResponse[RDF.Graph[Rdf]]] =
      import h4s.CacheDirective.`only-if-cached`
      // we don't need the `Accept` headers as those are added by the cache middleware
      val headers =
        if onlyCache then h4s.Headers(h4s.headers.`Cache-Control`(`only-if-cached`))
        else h4s.Headers.empty
      iClient.run(h4s.Request(method = method, uri = url.toh4.withoutFragment, headers = headers))
        .map(cachedRes => cachedRes.map(_.resolveAgainst(url)))

   /** we find ACL by going to the web */
   def findAclFor(
       requestUrl: ll.AbsoluteUrl,
       responseHeaders: org.http4s.Headers,
       depth: Int = 50
   ): Resource[F, RDF.Graph[Rdf]] =
      val onlyCache: Boolean = false
      val links: Seq[Rel] = extractLinks(requestUrl, responseHeaders).sortBy(x => relPriority(x._1))
      val acl = links.collectFirst {
        case (Right(`defaultAC`), uri) => findAclForContainer(uri, onlyCache)
        case (Right("acl"), uri) => fetchAclGr(uri, onlyCache)
      }
      acl.getOrElse {
        findContainerFor(requestUrl, links, onlyCache, depth) match
         case None => Resource.raiseError[F, RDF.Graph[Rdf], Throwable](
             Exception(s"No useable link to find ACL from $requestUrl")
           )
         case Some(url) => findAclFor(url, responseHeaders, depth - 1)
      }
   end findAclFor

   /** when looking in the cache we never assume that no info is bad info, we just continue looking,
     * so we don't even assume we already have the data of the first request. (hence no headers)
     * todo: see if we can combine this function and findAclFor into one function to avoid
     * duplication
     */
   def findCachedAclFor(
       requestUrl: ll.AbsoluteUrl,
       depth: Int = 50
   ): Resource[F, RDF.Graph[Rdf]] =
      val onlyCache: Boolean = true
      // we start by making a HEAD request, because we just want to find the link to the acl,
      // if requestUrl is the acl, then it may link back to itself, giving us what we want, or it
      // will link to nothing, in which case we won't notice which is a problem, in which case we won't notice which is a problem
      // todo: have acls link back to themselves on the server as per https://github.com/solid/authorization-panel/issues/189
      //   or find another way to allow the client to tell it is on an acl.
      fetchRdf(requestUrl, HEAD, onlyCache).flatMap { crG =>
         val links = extractLinks(requestUrl, crG.headers)
         // we signal an continuable failure by plaing it in the internal Option
         val defaultOrRel: Resource[F, Option[RDF.Graph[Rdf]]] =
           if onlyCache && crG.status == h4s.Status.GatewayTimeout
           then // we retrun a resource with a None, meaning we can continue looking in the cache
              Resource.liftK(fc.pure[Option[RDF.Graph[Rdf]]](None))
           else
              for
                 optDeflt <- links.collectFirst { case (Right(`defaultAC`), uri) =>
                   findAclForContainer(uri, onlyCache)
                 }.sequence
                 // if the defaultAC link above fails we fail otherwise
                 optEAcl <- optDeflt match
                  case Some(_) => optDeflt.map(Right[Throwable, RDF.Graph[Rdf]])
                      .pure[Resource[F, _]]
                  case None => links.collectFirst { case (Right("acl"), uri) =>
                      fetchAclGr(uri, onlyCache)
                    }.map(_.attempt).sequence
              yield optEAcl.flatMap(_.toOption)

         // fetching acl directly can fail we should continue if it does
         defaultOrRel.flatMap {
           case Some(gr) => gr.pure[Resource[F, _]]
           case None => findContainerFor(requestUrl, links, onlyCache, depth) match
              case None => Resource.raiseError[F, RDF.Graph[Rdf], Throwable](
                  Exception(s"No useable link to find ACL from $requestUrl")
                )
              case Some(url) => findCachedAclFor(url, depth - 1)
         }
      }
   end findCachedAclFor

   /** @param requestUrl
     *   the original request url
     * @param links
     *   the links found in the response or previous request on requestUrl
     * @param onlyCache
     *   if true, only the cache is searched
     * @param depth
     *   the depth to search for ldp:contains reverse relations, after which URL hierarchy can be
     *   looked at if in onlyCache mode
     * @return
     *   container for given URL looking at ldp:contains relation in header or if none is found in
     *   onlyCache mode looking at parent of requestUrl
     */
   def findContainerFor(
       requestUrl: ll.AbsoluteUrl,
       links: Seq[Rel],
       onlyCache: Boolean = false,
       depth: Int
   ): Option[ll.AbsoluteUrl] = links.collectFirst {
     case (Right(`ldpContains`), uri) if depth >= 0 => uri.resolve(requestUrl).toAbsoluteUrl
   }.orElse {
     if onlyCache
     then // we don't need to count depth on onlycache as Urls have a pragmetic size limit
        requestUrl.parent
     else None
   }

   /** given the original request and a response, return the correctly signed original request (test
     * for the HttpSig WWW-Authenticate header has been done before calling this function)
     */
   def httpSigChallenge(
       lastReqUrl: ll.AbsoluteUrl, // http4s.Request objects are not guaranteed to contain absolute urls
       originalRequest: h4s.Request[F],
       response: h4s.Response[F],
       nel: NonEmptyList[h4s.Challenge]
   ): F[h4s.Request[F]] =
      import BasicWallet.*
      val result: Resource[F, h4s.Request[F]] = findAclFor(lastReqUrl, response.headers)
        .flatMap(signRequest(originalRequest, lastReqUrl))
      result.use(fc.pure)
   end httpSigChallenge

   /** sign the request with the first key that matches the rules in the aclGraph. (We pass the
     * requestUrl too to avoid recaculating it from the request...)
     */
   def signRequest(
       originalRequest: h4s.Request[F],
       originalRequestUrl: ll.AbsoluteUrl
   )(
       aclGr: RDF.Graph[Rdf]
   ): Resource[F, h4s.Request[F]] =
      import io.lemonlabs.uri.config.UriConfig
      val reqRes = ops.URI(originalRequestUrl.copy(fragment = None)(UriConfig.default))
      val keyNodes: Iterator[St.Subject[Rdf]] =
        for
           agentNode <- findAgents(aclGr, reqRes, originalRequest.method)
           controllerTriple <- aclGr.find(*, sec.controller, agentNode)
        yield controllerTriple.subj

      import run.cosy.http4s.Http4sTp.given
      val keys: Iterable[KeyData[F]] = keyNodes.collect { case u: RDF.URI[Rdf] =>
        keyIdDB.find(kid => kid.keyIdAtt.value.asciiStr == u.value).toList
      }.flatten.to(Iterable)

      val x: F[h4s.Request[F]] =
        for
           keydt <- fc.fromOption[KeyData[F]](
             keys.headOption,
             Exception(
               s"none of our keys fit the ACL for resource $originalRequestUrl accessed in " +
                 s"${originalRequest.method} matches the rules in graph { $aclGr } "
             )
           )
           signingFn <- keydt.signer
           now <- clock.realTime // <- todo, add clock time caching perhaps
           signedReq <- MessageSignature.withSigInput[F, H4](
             originalRequest.asInstanceOf[Http.Request[H4]],
             Rfc8941.Token("sig1"),
             keydt.mkSigInput(now),
             signingFn
           )
        yield
           val res = run.cosy.http4s.Http4sTp.hOps
             .addHeader[Http.Request[H4]](signedReq)("Authorization", "HttpSig proof=sig1")
           h4ReqToHttpReq(res)
      Resource.liftK(x)
   end signRequest

   /** This is different from middleware such as FollowRedirects, as that essentially continues the
     * request. Here we need to stop the request and make new ones to find the access control rules
     * for the given resource. (that could just be a BasicAuth request for a password, or a more
     * complex description linked to from the resource, but it may also just involve querying a DB
     * or quad store.)
     */
   override def sign(
       failed: h4s.Response[F],
       lastReq: h4s.Request[F]
   ): F[h4s.Request[F]] =
      import cats.syntax.applicativeError.given
      failed.status.code match
       case 402 => fc.raiseError(
           new Exception("We don't support payment authentication yet")
         )
       case 401 => failed.headers.get[h4hdr.`WWW-Authenticate`] match
          case None => fc.raiseError(
              new Exception("No WWW-Authenticate header. Don't know how to login")
            )
          case Some(h4hdr.`WWW-Authenticate`(nel)) => // do we recognise a method?
            for
               lastReqUrl <- fc.fromTry(Try(lastReq.uri.toLL.toAbsoluteUrl))
               authdReq <- fc.fromTry(basicChallenge(lastReqUrl.authority, lastReq, nel))
                 .handleErrorWith { _ =>
                   httpSigChallenge(lastReqUrl, lastReq, failed, nel)
                 }
            yield authdReq
       case _ => ??? // fail
   end sign

   override def signFromDB(req: h4s.Request[F]): F[Either[Throwable, h4s.Request[F]]] =
     // todo: the DB needs to keep track of what WWW-Authenticate methods the server allows.
     // These will be difficult to find in the headers, as the 401 in which they appeared may be
     // somewhere completely different.
     // I will assume that the server can do HTTP-Sig for the moment
     // todo: fix!!!
     // todo: as we endup with F[Request] do we need Resources everywhere here?
     findCachedAclFor(req.uri.toLL.toAbsoluteUrl, 100).flatMap {
       signRequest(req, req.uri.toLL.toAbsoluteUrl)
     }.attempt.use(fc.pure)

end BasicWallet
