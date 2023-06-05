package net.bblfish.ldp.cmd

import org.http4s.Uri
import org.http4s.Status
import org.http4s.Headers
import org.http4s.Response
import cats.free.Free

/** LDP Commands for a free monad and free applicatives. (see "An Intuitive Guide to Combining Free
  * Monads and Free Applicatives" https://twitter.com/bblfish/status/1587052879986180097 )
  *
  * What do we need? We need GET/POST/PUT/DELETE/QUERY And we are *very* often interested in RDF
  * Graphs being returned. But we canno Similar to an Http Request but where we sometimes want to be
  * able to avoid the serialisation part.
  */
sealed trait LdpCmd[A]:
   /* All comands are aimed at a URL.
    * todo: consider relative URLs */
   def url: Uri

/** Get request from URL where we expect a graph response.
  *
  * We don't start with a generalised version, to keep things simple.
  *
  * todo: ideas: one may want to pass
  *   - etag, date-since-previous-version,
  *   - authenticated agent info,
  *   - content-type?
  *   - ... It captures a request that reutrns a Response, where k is a function to transform that
  *     response into any A
  */
case class GetGraph[A](url: Uri, k: GraphResponse => A) extends LdpCmd[A]

/** A response to a request. The Metadata tells if the response succeeded, what the problems may
  * have been, etc... The content, is an attempted parse of the stream to the object type. (todo?:
  * generalise the type of the content to quite a lot more types, such as DataSets, Images, streams,
  * ...)
  *
  * todo: should the result type be generic? In which case this would be a generalisation of http4s.
  */
case class GraphResponse[R <: RDF](meta: Meta, content: Try[RDF.Graph[R]])

/** Metadata on the resource from the server.
  * @param url
  *   the URL of the resource
  * @param status
  *   status of the response
  * @param headers
  *   Headers on the response
  */
case class Meta(url: Uri, status: Status = Status.Ok, headers: Headers)

object LdpCmd:
// type NamedGraph = (Uri, Rdf#Graph)
// type NamedGraphs = Map[Uri,Rdf#Graph]

   type Script[A] = cats.free.Free[LDPCmd, A]

   def pure[A](a: A): Script[A] = Free.pure(a)

   def get(key: Uri): Script[Response] = Free.liftF[LdpCmd, Response](Get[Response](key, identity))
