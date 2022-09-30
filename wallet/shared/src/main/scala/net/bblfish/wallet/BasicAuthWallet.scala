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
import cats.effect.{Clock, Concurrent, IO}
import cats.implicits.*
import io.lemonlabs.uri as ll
import net.bblfish.app.Wallet
import org.http4s.*
import org.http4s.client.Client
import org.http4s.headers.{Authorization, *}
import org.http4s.BasicCredentials
import org.w3.banana.http4sIO.RDFDecoders
import org.w3.banana.io.{JsonLd, RDFReader, Turtle}
import org.w3.banana.prefix.Cert
import org.w3.banana.prefix.WebACL
import org.w3.banana.prefix.WebACL.apply
import org.w3.banana.{Ops, RDF}
import run.cosy.http.headers.Rfc8941.{IList, SfInt}
import run.cosy.http.headers.{Rfc8941, SelectorOps, SigInput}
import scodec.bits.ByteVector
import run.cosy.http4s.auth.Http4sMessageSignature
import run.cosy.http.headers.MessageSelector
import run.cosy.http.headers.DictSelector
import run.cosy.http4s.headers.BasicHeaderSelector
import run.cosy.http4s.headers.Http4sDictSelector
import run.cosy.http4s.Http4sTp.H4
import run.cosy.http.Http

import scala.util.{Failure, Try}

class BasicId(val username: String, val password: String)

/* The keyId points to the public key, but the private key is obtained elsewhere, and
 * should also have a URL.
 * todo: the Public Key should be linked to the algorithm string
 *    so that it can be added when needed as `alg="..."` to the
 *    signature. (It should also be found in the remote jwt)
 * */
class KeyId[F[_]: Concurrent : Clock, Rdf <: RDF](
  val keyId: RDF.URI[Rdf],
  val signer: F[ByteVector => F[ByteVector]],
  val alg: String = "" ):
 
  //wrapped in an F, because of clock
  // is this a val or a function? A val should do...
  def mkSigInput(): F[SigInput] =
    for
      t <- summon[Clock[F]].realTime
      si <- summon[Concurrent[F]].fromOption(
         SigInput(IList()(SigInput.createdTk -> SfInt(t.toSeconds))),
         Exception("SigInput template faulty")) //todo: we need a SigInputBuilder
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
    keyIdDB: Map[RDF.URI[Rdf], KeyId[F, Rdf]]
)(using
    client: Client[F],
    ops: Ops[Rdf],
    rdfDecoders: RDFDecoders[F, Rdf],
    fc: Concurrent[F]
) extends Wallet[F]:

  import ops.{*, given}
  import rdfDecoders.allrdf

  import run.cosy.http4s.auth.Http4sMessageSignature.withSigInput
  implicit def reqToHttpReq(h4req: org.http4s.Request[F]): Http.Request[F, H4] = h4req.asInstanceOf[Http.Request[F, H4]]
  
  given selectorOps: SelectorOps[Http.Request[F, H4]] = new run.cosy.http4s.headers.Http4sMessageSelectors[F](
    true,
    Uri.Authority(),
    443
  ).requestSelectorOps
  
  val WAC: WebACL[Rdf] = WebACL[Rdf]
  val cert: Cert[Rdf] = Cert[Rdf]

  //todo: here we assume the request Uri usually has the Host. Need to verify, or pass the full Url
  def basicChallenge(
      host: ll.Authority, //request for original Host
      originalRequest: org.http4s.Request[F],
      nel: NonEmptyList[Challenge]
  ): Try[org.http4s.Request[F]] =
    //todo: can one use some of the info in the Challenge?
    val either = for
      _    <- nel.find(_.scheme == "Basic").toRight(Exception("server does not make basic auth available"))
      id   <- db.get(host).toRight(Exception("no passwords for hot "+host))
    yield originalRequest.withHeaders(Authorization(BasicCredentials(id.username, id.password)))
    either.toTry
  end basicChallenge

  def httpSigChallenge(
      requestUrl: ll.AbsoluteUrl, // http4s.Request objects are not guaranteed to contain absolute urls
      originalRequest: org.http4s.Request[F],
      response: Response[F],
      nel: NonEmptyList[Challenge]
  ): F[org.http4s.Request[F]] =
    val aclLinks: Seq[LinkValue] =
      for
        httpSig <- nel.find(_.scheme == "HttpSig").toList
        link <- response.headers.get[Link].toList
        linkVal <- link.values.toList
        if linkVal.rel == Some("acl") //todo: also check other wac url
      yield linkVal

    aclLinks.headOption match //todo: we try to the first only but what if...
      case None =>
        fc.raiseError(Exception(
            Exception("no acl Link in header. Cannot find where the rules are.")
          ))
      case Some(link) =>
        client.fetchAs[RDF.rGraph[Rdf]](Request(
              uri = link.uri.withoutFragment,
              headers = Headers(rdfDecoders.allRdfAccept)
            ))
          .flatMap { (rG: RDF.rGraph[Rdf]) =>
            //todo: what if original url is relative?
            val g: RDF.Graph[Rdf] = rG.resolveAgainst(requestUrl)
            val reqRes = ops.URI(originalRequest.uri.toString) // <--

            // this requires all the info to be in the same graph. Needs generalisation to
            // jump across graphs
            val keyNodes = for
              tr <- g.find(`*`, WAC.accessTo, reqRes)
              if !g
                .find(tr.subj, WAC.mode, modeForMethod(originalRequest.method))
                .isEmpty //todo: too simple
              obj <- g.find(tr.subj, WAC.agent, `*`).map(_.obj)
                .collect[RDF.Statement.Subject[Rdf]]{
                  case u: RDF.URI[Rdf]  => u
                  case b: RDF.BNode[Rdf] => b
                }
              tr3 <- g.find(obj, cert.key, `*`)
            yield tr3.obj

            val keys: List[KeyId[F, Rdf]] = keyNodes.toList.collect {
              case u: RDF.URI[Rdf] => keyIdDB.get(u).toList
            }.flatten

            for
              key <- fc.fromOption[KeyId[F, Rdf]](keys.headOption,
                Exception("no key in the ACL matches the keys we have available"))
              signingFn <- key.signer
              sigIn <- key.mkSigInput()
              signedReq <- originalRequest.withSigInput(
                Rfc8941.Token("sig1"), sigIn, signingFn
              )
            yield signedReq
          }
  end httpSigChallenge

  def modeForMethod(method: Method): RDF.URI[Rdf] =
    if List(Method.GET, Method.HEAD, Method.SEARCH).contains(method) then
      WAC.Read
    else WAC.Write

  //ignoring username:password urls
  def http4sUrlToLLUrl(u: org.http4s.Uri): ll.Url =
    import u.{
      host as h4host,
      path as h4path,
      query as h4query,
      port as h4port,
      query as h4query,
      scheme as h4scheme
    }
    ll.Url(
      h4scheme.map(_.value).getOrElse(null),
      null,
      null,
      h4host.map(_.value).getOrElse(null),
      h4port.getOrElse(-1),
      h4path.toString,
      ll.QueryString.parse(h4query.toString),
      null
    )

  /** This is different from middleware such as FollowRedirects, as that
    * essentially continues the request. Here we need to stop the request and
    * make new ones to find the access control rules for the given resource.
    * (that could just be a BasicAuth request for a password, or a more complex
    * description linked to from the resource, but it may also just involve
    * querying a DB or quad store.)
    */
  override def sign(
    res: Response[F],
    req: org.http4s.Request[F],
    originalReqUrl: ll.AbsoluteUrl
  ): F[org.http4s.Request[F]] =
    res.status.code match
      case 402 =>
        fc.raiseError(
          new Exception("We don't support payment authentication yet")
        )
      case 401 =>
        res.headers.get[`WWW-Authenticate`] match
          case None =>
            fc.raiseError(
                new Exception("No WWW-Authenticate header. Don't know how to login")
            )
          case Some(`WWW-Authenticate`(nel)) => // do we recognise a method?
            val bf = fc.fromTry(
              basicChallenge(originalReqUrl.authority, req, nel)
            )
            fc.handleErrorWith(bf){ _ =>
              httpSigChallenge(originalReqUrl, req, res, nel)
            }
      case _ => ??? //fail
  end sign

end BasicWallet
