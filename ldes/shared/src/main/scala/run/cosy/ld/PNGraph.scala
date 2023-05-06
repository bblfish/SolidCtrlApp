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

package run.cosy.ld

import cats.MonadError
import cats.effect.kernel.Concurrent
import fs2.Pure
import org.w3.banana.RDF.{Graph, Node, Statement, URI}
import org.w3.banana.{Ops, RDF}

trait Web[F[_]: Concurrent, R <: RDF]:
  /** get a Graph for the given URL (without a fragment) */
  def get(url: RDF.URI[R]): F[RDF.Graph[R]]

  /** get Pointed Named Graph for given url */
  def getPNG(url: RDF.URI[R]): F[UriNGraph[R]]

/** A Pointed Named Graph, ie, a pointer into a NamedGraph We don't use a case class here as
  * equality between PNGgraphs is complicated by the need to prove isomorphism between graphs, and
  * the nodes have to be equivalent.
  *
  * todo: it would make sense to have subtypes for different types of points, so that filters on
  * points that are urls can be typed. Also when the point is on the literal then / makes less
  * sense. png.jump makes sense only if the node is a URL
  */
trait PNGraph[R <: RDF]:
  type Point <: RDF.URI[R] | RDF.Literal[R] | RDF.BNode[R]
  def point: Point
  def name: URI[R]
  def graph: Graph[R]

  /** collect from the given point relations forward and backward (where possible) and return a
    * graph
    */
  def collect(
      forward: RDF.URI[R]*
  )(
      backward: RDF.URI[R]*
  )(using ops: Ops[R]): RDF.Graph[R] =
    import ops.{*, given}
    val f: Seq[Seq[RDF.Triple[R]]] =
      if point.isURI || point.isBNode then
        for frel <- forward
        yield graph.find(point, frel, `*`).toSeq
      else Seq.empty
    val b: Seq[Seq[RDF.Triple[R]]] =
      for brel <- backward
      yield graph.find(`*`, brel, point).toSeq
    Graph(f.flatten ++ b.flatten)

trait SubjPNGraph[R <: RDF] extends PNGraph[R]:
  type Point <: RDF.Statement.Subject[R]

  def /(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
    import ops.{*, given}
    graph
      .find(point, rel, `*`)
      .map { (tr: RDF.Triple[R]) =>
        tr.obj.asNode.fold(
          uri => new UriNGraph(uri, name, graph),
          bn => new BNodeNGraph[R](bn, name, graph),
          lit => new LiteralNGraph[R](lit, name, graph)
        )
      }
      .toSeq
  end /

  inline def rel(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] = /(rel)

  def \(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
    import ops.{*, given}
    graph
      .find(`*`, rel, point)
      .map { (tr: RDF.Triple[R]) =>
        val s: Statement.Subject[R] = tr.subj
        s.foldSubj[PNGraph[R]](
          uri => new UriNGraph(uri, name, graph),
          bn => new BNodeNGraph[R](bn, name, graph)
        )
      }
      .toSeq
  end \

  def hasType(tp: RDF.URI[R])(using ops: Ops[R]): Boolean =
    import ops.{*, given}
    val it: Iterator[RDF.Triple[R]] = graph.find(point, rdf.typ, tp)
    it.hasNext

end SubjPNGraph

/** A PNG where the point is a URI */
case class UriNGraph[R <: RDF](
    val point: URI[R],
    val name: URI[R],
    val graph: Graph[R]
)(using ops: Ops[R])
    extends SubjPNGraph[R]:
  import ops.{*, given}
  type Point = RDF.URI[R]

  lazy val pointLoc: RDF.URI[R] = point.fragmentLess
  lazy val isLocal: Boolean = pointLoc == name

//todo: things are more complex when one takes redirects into account...
  def jump[F[_]: Concurrent](using web: Web[F, R]): F[UriNGraph[R]] =
    import cats.syntax.functor.*
    import ops.{*, given}
    if isLocal then summon[Concurrent[F]].pure(this)
    else web.get(pointLoc).map(g => new UriNGraph(point, pointLoc, g))

end UriNGraph

/** A PNG where the point is a BNode */
class BNodeNGraph[R <: RDF](
    val point: RDF.BNode[R],
    val name: URI[R],
    val graph: Graph[R]
) extends SubjPNGraph[R]:
  type Point = RDF.BNode[R]

/** A PNG where the point is a BNode */
class LiteralNGraph[R <: RDF](
    val point: RDF.Literal[R],
    val name: URI[R],
    val graph: Graph[R]
) extends PNGraph[R]:
  type Point = RDF.Literal[R]

  // todo: this is duplicated code with SubjPNGraph.
  def \(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
    import ops.{*, given}
    graph
      .find(`*`, rel, point)
      .map { (tr: RDF.Triple[R]) =>
        tr.subj.foldSubj(
          uri => UriNGraph(uri, name, graph),
          bn => BNodeNGraph(bn, name, graph)
        )
      }
      .toSeq
end LiteralNGraph

object PNGraph:

  extension [F[_]: cats.effect.Concurrent, R <: RDF](
      pngs: Seq[PNGraph[R]]
  )(using web: Web[F, R], ops: Ops[R])
    // jump on nodes in the sequence that can be jumped
    def jump: fs2.Stream[F, PNGraph[R]] =
      import ops.given
      var local: List[PNGraph[R]] = Nil
      var remoteGs: List[UriNGraph[R]] = Nil
      pngs.foreach {
        case ug: UriNGraph[R] if !ug.isLocal => remoteGs = ug :: remoteGs
        case other                           => local = other :: local
      }
      import cats.syntax.all.given

      val in: fs2.Stream[Pure, PNGraph[R]] = fs2.Stream.chunk(fs2.Chunk.seq(local))
      val out: fs2.Stream[F, PNGraph[R]] =
        fs2.Stream
          .emits(remoteGs)
          // todo: group the urls by domain (groupAdjacentBy) and run each with two threads max
          .parEvalMapUnordered(5) { (remote: UriNGraph[R]) =>
            // todo: we should deal with errors fetching graphs
            // web should perhaps return a UriNGraph
            web.get(remote.pointLoc).map(g => UriNGraph[R](remote.point, remote.pointLoc, g))
          }
      in ++ out
    end jump

    def rel(relation: RDF.URI[R]): Seq[PNGraph[R]] =
      pngs.collect { case sub: SubjPNGraph[R] =>
        sub / relation
      }.flatten

    def filterType(tp: RDF.URI[R]): Seq[SubjPNGraph[R]] =
      pngs.collect {
        case sub: SubjPNGraph[R] if sub.hasType(tp) => sub
      }

  extension [F[_]: cats.effect.Concurrent, R <: RDF](
      pngs: fs2.Stream[F, PNGraph[R]]
  )(using ops: Ops[R])

    def /(rel: RDF.URI[R]): fs2.Stream[F, PNGraph[R]] =
      pngs.collect { case sub: SubjPNGraph[R] =>
        fs2.Stream(sub / rel*)
      }.flatten

    inline def rel(rel: RDF.URI[R]): fs2.Stream[F, PNGraph[R]] = /(rel)

    def jump(using Web[F, R]): fs2.Stream[F, SubjPNGraph[R]] =
      pngs.collect {
        case bn: BNodeNGraph[R] => fs2.Stream(bn)
        case un: UriNGraph[R]   => fs2.Stream.eval(un.jump)
      }.flatten

    def filterType(tp: RDF.URI[R]): fs2.Stream[F, SubjPNGraph[R]] =
      pngs.collect {
        case sub: SubjPNGraph[R] if sub.hasType(tp) => sub
      }
