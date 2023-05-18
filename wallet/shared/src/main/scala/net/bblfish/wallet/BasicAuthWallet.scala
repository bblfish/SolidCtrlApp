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
   val effectiveAclLink = "effectiveAccessControl"
   val EffectiveAclOpt = Some(effectiveAclLink)
   val aclRelTypes = List(EffectiveAclOpt, "acl", effectiveAclLink)

/** place code that only needs RDF and ops here. */
class WalletTools[Rdf <: RDF](using ops: Ops[Rdf]):
   import run.cosy.web.util.UrlUtil.*
   import ops.{*, given}

   val wac: WebACL[Rdf] = WebACL[Rdf]
   val foaf = prefix.FOAF[Rdf]
   val sec: SecurityPrefix[Rdf] = SecurityPrefix[Rdf]

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
  * @param client
  *   The client needed to fetch acl resources on the web (should be a proxy)
  */
class BasicWallet[F[_], Rdf <: RDF](
    db: Map[ll.Authority, BasicId],
    keyIdDB: Seq[KeyData[F]] = Seq()
)(client: Client[F])(using
    ops: Ops[Rdf],
    rdfDecoders: RDFDecoders[F, Rdf],
    fc: Concurrent[F],
    clock: Clock[F]
) extends Wallet[F]:
   val wt: WalletTools[Rdf] = new WalletTools[Rdf]

   val reqSel: ReqSelectors[H4] = new ReqSelectors[H4](using new SelectorFnsH4())
   import ops.{*, given}
   import rdfDecoders.allrdf
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

   def httpSigChallenge(
       requestUrl: ll.AbsoluteUrl, // http4s.Request objects are not guaranteed to contain absolute urls
       originalRequest: h4s.Request[F],
       response: h4s.Response[F],
       nel: NonEmptyList[h4s.Challenge]
   ): F[h4s.Request[F]] =
      import BasicWallet.*
      val aclLinks: List[LinkValue] =
        for
           httpSig <- nel.find(_.scheme == "HttpSig").toList
           link <- response.headers.get[Link].toList
           linkVal <- link.values.toList
           rel <- linkVal.rel.toList
           if aclRelTypes.contains(rel)
        yield linkVal
      aclLinks.find(_.rel == EffectiveAclOpt).orElse(aclLinks.headOption) match
       case None => fc
           .raiseError(Exception("no acl Link in header. Cannot find where the rules are."))
       case Some(linkVal) =>
         val h4req = llUrltoHttp4s(requestUrl)
         val absLink: h4s.Uri = h4req.resolve(linkVal.uri)
         client.fetchAs[RDF.rGraph[Rdf]](
           h4s.Request(
             uri = absLink.withoutFragment
           )
         ).flatMap { (rG: RDF.rGraph[Rdf]) =>
            // todo: what if original url is relative?
            val lllink = http4sUrlToLLUrl(absLink).toAbsoluteUrl
            val g: RDF.Graph[Rdf] = rG.resolveAgainst(lllink)
            val reqRes = ops.URI(originalRequest.uri.toString) // <--
            // this requires all the info to be in the same graph. Needs generalisation to
            // jump across graphs
            val keyNodes: Iterator[St.Subject[Rdf]] =
              for
                 agentNode <- findAgents(g, reqRes, originalRequest.method)
                 controllerTriple <- g.find(*, sec.controller, agentNode)
              yield controllerTriple.subj

            import run.cosy.http4s.Http4sTp.given
            val keys: Iterable[KeyData[F]] = keyNodes.collect { case u: RDF.URI[Rdf] =>
              keyIdDB.find(kid => kid.keyIdAtt.value.asciiStr == u.value).toList
            }.flatten.to(Iterable)

            for
               keydt <- fc.fromOption[KeyData[F]](
                 keys.headOption,
                 Exception(
                   s"none of our keys fit the ACL $lllink for resource $reqRes accessed in " +
                     s"${originalRequest.method} matches the rules in { $g } "
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
         }
   end httpSigChallenge

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
               url <- fc.fromTry(Try(http4sUrlToLLUrl(lastReq.uri).toAbsoluteUrl))
               authdReq <- fc.fromTry(basicChallenge(url.authority, lastReq, nel))
                 .handleErrorWith { _ =>
                   httpSigChallenge(url, lastReq, failed, nel)
                 }
            yield authdReq
       case _ => ??? // fail
   end sign

   override def signFromDB(req: h4s.Request[F]): F[h4s.Request[F]] = fc.point(req)

end BasicWallet
