/*
 * Copyright 2021 Typelevel
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
import cats.effect.Clock
import cats.effect.Concurrent
import cats.effect.IO
import cats.implicits.*
import com.apicatalog.jsonld.uri.UriUtils
import io.lemonlabs.uri as ll
import io.lemonlabs.uri.Url
import net.bblfish.app.Wallet
import net.bblfish.web.util.SecurityPrefix
import net.bblfish.web.util.UrlUtil.{http4sUrlToLLUrl, llUrltoHttp4s}
import org.http4s as h4s
import org.http4s.client.Client
import org.http4s.headers as h4hdr
import org.http4s.headers.Authorization
import org.w3.banana.Ops
import org.w3.banana.RDF
import org.w3.banana.http4sIO.RDFDecoders
import org.w3.banana.io.JsonLd
import org.w3.banana.io.RDFReader
import org.w3.banana.io.Turtle
import org.w3.banana.prefix.Cert
import org.w3.banana.prefix.WebACL
import org.w3.banana.prefix.WebACL.apply
import run.cosy.http.Http
import run.cosy.http.headers.DictSelector
import run.cosy.http.headers.MessageSelector
import run.cosy.http.headers.Rfc8941
import run.cosy.http.headers.Rfc8941.IList
import run.cosy.http.headers.Rfc8941.{SfInt,SfString}
import run.cosy.http.headers.SelectorOps
import run.cosy.http.headers.SigInput
import run.cosy.http4s.Http4sTp.HT as H4
import run.cosy.http4s.auth.Http4sMessageSignature
import run.cosy.http4s.headers.BasicHeaderSelector
import run.cosy.http4s.headers.Http4sDictSelector

import scala.reflect.TypeTest
import scala.util.Failure
import scala.util.Try
import scodec.bits.ByteVector

class BasicId(val username: String, val password: String)

/* The keyId points to the public key, but the private key is obtained elsewhere, and
 * should also have a URL.
 * todo: the Public Key should be linked to the algorithm string
 *    so that it can be added when needed as `alg="..."` to the
 *    signature. (It should also be found in the remote jwt)
 * */
class KeyId[F[_]: Concurrent: Clock, Rdf <: RDF](
    val keyId: RDF.URI[Rdf],
    val signer: F[ByteVector => F[ByteVector]],
    val alg: String = ""
)(using ops: Ops[Rdf]):
  import ops.given
  // wrapped in an F, because of clock
  // is this a val or a function? A val should do...
  def mkSigInput(): F[SigInput] =
    for
      t <- summon[Clock[F]].realTime
      si <- summon[Concurrent[F]].fromOption(
        SigInput(IList()(SigInput.createdTk -> SfInt(t.toSeconds), SigInput.keyidTk -> SfString(keyId.value))),
        Exception("SigInput template faulty")
      ) // todo: we need a SigInputBuilder
    yield si

end KeyId

/*
 * First attempt at a Wallet, just to get things going.
 * The wallet must be given callections of passwords for domains, KeyIDs for
 * http signatures
 * it needs a client to follow links to the WAC rules, though it would be better
 * if instead it were given a DataSet proxied to the web to allow caching.
 */
class BasicWallet[F[_], Rdf <: RDF](
    db: Map[ll.Authority, BasicId],
    keyIdDB: Seq[KeyId[F, Rdf]]
)(using
    client: Client[F],
    ops: Ops[Rdf],
    rdfDecoders: RDFDecoders[F, Rdf],
    fc: Concurrent[F]
) extends Wallet[F]:

  import ops.*
  import ops.given
  import rdfDecoders.allrdf
  import run.cosy.http4s.auth.Http4sMessageSignature.withSigInput
  import scala.language.implicitConversions

  def reqToH4Req(h4req: h4s.Request[F]): Http.Request[F, H4] =
    h4req.asInstanceOf[Http.Request[F, H4]]

  def h4ReqToHttpReq(h4req: Http.Request[F, H4]): h4s.Request[F] =
    h4req.asInstanceOf[h4s.Request[F]]

  //todo: I am mixing gerneic programming here and then fixing one type here. Not that good.
  given selectorOps: SelectorOps[Http.Request[F, H4]] =
    new run.cosy.http4s.headers.Http4sMessageSelectors[F](
      true,
      h4s.Uri.Authority(),
      443
    ).requestSelectorOps
    

  val WAC: WebACL[Rdf] = WebACL[Rdf]
  val sec: SecurityPrefix[Rdf] = SecurityPrefix[Rdf]

  // todo: here we assume the request Uri usually has the Host. Need to verify, or pass the full Url
  def basicChallenge(
      host: ll.Authority, // request for original Host
      originalRequest: h4s.Request[F],
      nel: NonEmptyList[h4s.Challenge]
  ): Try[h4s.Request[F]] =
    // todo: can one use some of the info in the Challenge?
    val either = for
      _ <- nel
        .find(_.scheme == "Basic")
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
  )(
      using // todo, why do I have to explicitly use the type test?
      tt: TypeTest[RDF.Statement.Object[Rdf], RDF.URI[Rdf]]
  ): F[h4s.Request[F]] =
    val aclLinks: Seq[h4hdr.LinkValue] =
      for
        httpSig <- nel.find(_.scheme == "HttpSig").toList
        link <- response.headers.get[h4hdr.Link].toList
        linkVal <- link.values.toList
        if linkVal.rel == Some("acl") // todo: also check other wac url
      yield linkVal

    aclLinks.headOption match // todo: we try to the first only but what if...
      case None =>
        fc.raiseError(
          Exception(
            Exception("no acl Link in header. Cannot find where the rules are.")
          )
        )
      case Some(link) =>
        val h4req = llUrltoHttp4s(requestUrl)
        val absLink: h4s.Uri = h4req.resolve(link.uri)
        client
          .fetchAs[RDF.rGraph[Rdf]](
            h4s.Request(
              uri = absLink.withoutFragment,
              headers = h4s.Headers(rdfDecoders.allRdfAccept)
            )
          )
          .flatMap { (rG: RDF.rGraph[Rdf]) =>
            // todo: what if original url is relative?
            val lllink = http4sUrlToLLUrl(absLink).toAbsoluteUrl
            val g: RDF.Graph[Rdf] = rG.resolveAgainst(lllink)
            val reqRes = ops.URI(originalRequest.uri.toString) // <--
            // this requires all the info to be in the same graph. Needs generalisation to
            // jump across graphs
            val mode = modeForMethod(originalRequest.method)
            val keyNodes = for
              tr <- g.find(`*`, WAC.accessTo, reqRes)
              if !g
                .find(tr.subj, WAC.mode, mode)
                .isEmpty // todo: too simple
              obj <- g
                .find(tr.subj, WAC.agent, `*`)
                .map(_.obj)
                .collect[RDF.Statement.Object[Rdf]] {
                  case tt(u)             => u
                  case b: RDF.BNode[Rdf] => b
                  // todo: the key could be in a literal too !?
                }
              tr3 <- g.find(*, sec.controller, obj)
            yield tr3.subj
            import run.cosy.http4s.Http4sTp.given
            val keys: List[KeyId[F, Rdf]] = keyNodes.toList.collect { case tt(u) =>
              keyIdDB.find(kid => kid.keyId == u).toList
            }.flatten

            for
              key <- fc.fromOption[KeyId[F, Rdf]](
                keys.headOption,
                Exception(
                  s"none of our keys fit the ACL $lllink for resource $reqRes accessed in $mode matches the rules in { $g } "
                )
              )
              signingFn <- key.signer
              sigIn <- key.mkSigInput()
              signedReq <- reqToH4Req(originalRequest).withSigInput(
                Rfc8941.Token("sig1"),
                sigIn,
                signingFn
              )
            yield
              val res = run.cosy.http4s.Http4sTp.hOps.addHeader[F, Http.Request[F,H4]](signedReq)("Authorization","HttpSig proof=sig1")
              h4ReqToHttpReq(res)
          }
  end httpSigChallenge

  def modeForMethod(method: h4s.Method): RDF.URI[Rdf] =
    import h4s.Method.{GET, HEAD, SEARCH}
    if List(GET, HEAD, SEARCH).contains(method) then WAC.Read
    else WAC.Write

  /** This is different from middleware such as FollowRedirects, as that essentially continues the
    * request. Here we need to stop the request and make new ones to find the access control rules
    * for the given resource. (that could just be a BasicAuth request for a password, or a more
    * complex description linked to from the resource, but it may also just involve querying a DB or
    * quad store.)
    */
  override def sign(
      res: h4s.Response[F],
      req: h4s.Request[F]
  ): F[h4s.Request[F]] =
    import cats.syntax.applicativeError.given
    res.status.code match
      case 402 =>
        fc.raiseError(
          new Exception("We don't support payment authentication yet")
        )
      case 401 =>
        res.headers.get[h4hdr.`WWW-Authenticate`] match
          case None =>
            fc.raiseError(
              new Exception("No WWW-Authenticate header. Don't know how to login")
            )
          case Some(h4hdr.`WWW-Authenticate`(nel)) => // do we recognise a method?
            for
              url <- fc.fromTry(Try(http4sUrlToLLUrl(req.uri).toAbsoluteUrl))
              authdReq <- fc.fromTry(basicChallenge(url.authority, req, nel)).handleErrorWith { _ =>
                httpSigChallenge(url, req, res, nel)
              }
            yield authdReq
      case _ => ??? // fail
  end sign

end BasicWallet
