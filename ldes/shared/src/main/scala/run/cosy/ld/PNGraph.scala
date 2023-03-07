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
 * todo: it would make sense to have subtypes for different types of points, so
 *    that filters on points that are urls can be typed. Also when the point is on the
 *    literal then / makes less sense. png.jump makes sense only if the node is a URL
  */
trait PNGraph[R <: RDF]:
  type Point <: Node[R]
  def point: Point
  def name: URI[R]
  def graph: Graph[R]
  
trait SubjPNGraph[R <: RDF] extends PNGraph[R]:
  type Point <: RDF.Statement.Subject[R]
  
  def /(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
     import ops.{*, given}
     graph.find(point, rel, `*`)
       .map { (tr: RDF.Triple[R]) =>
         tr.obj.asNode.fold(
           uri => new UriNGraph(uri,name,graph),
           bn => new BNodeNGraph[R](bn, name, graph),
           lit => new LiteralNGraph[R](lit, name, graph)
         )
       }
       .toSeq
  end /
  
  inline def rel(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] = /(rel)
  
  def \(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
    import ops.{*, given}
    graph.find(`*`, rel, point)
      .map { (tr: RDF.Triple[R]) =>
        val s: Statement.Subject[R] = tr.subj
        s.foldSubj[PNGraph[R]](
          uri => new UriNGraph(uri, name, graph),
          bn => new BNodeNGraph[R](bn, name, graph)
        )
      }
      .toSeq
  end \
  
end SubjPNGraph


/** A PNG where the point is a URI */
class UriNGraph[R <: RDF](
  val point: URI[R],
  val name: URI[R],
  val graph: Graph[R]
)(using ops: Ops[R]) extends SubjPNGraph[R]:
   import ops.{*,given}
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

  //todo: this is duplicated code with SubjPNGraph.
  def \(rel: RDF.URI[R])(using ops: Ops[R]): Seq[PNGraph[R]] =
    import ops.{*, given}
    graph.find(`*`, rel, point)
      .map { (tr: RDF.Triple[R]) =>
        tr.subj.foldSubj(
          uri => UriNGraph(uri, name, graph),
          bn => BNodeNGraph(bn, name, graph)
        )
      }.toSeq
end LiteralNGraph


object PNGraph:
  
  extension [F[_]: cats.effect.Concurrent, R <: RDF](
    pngs: Seq[PNGraph[R]]
  )(using web: Web[F, R], ops: Ops[R])
   
    def jump : fs2.Stream[F, PNGraph[R]] =
      import ops.given
      var local: List[PNGraph[R]] = Nil
      var remoteGs: List[UriNGraph[R]] = Nil
      pngs.foreach {
          case ug: UriNGraph[R] if !ug.isLocal => remoteGs = ug :: remoteGs
          case other => local = other::local
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


  extension[F[_] : cats.effect.Concurrent, R <: RDF] (
    pngs: fs2.Stream[F, PNGraph[R]]
  )(using ops: Ops[R])
 
    def /(rel: RDF.URI[R]): fs2.Stream[F, PNGraph[R]] =
      pngs.collect{
        case sub: SubjPNGraph[R] => fs2.Stream(sub/rel*)
      }.flatten

    inline def rel(rel: RDF.URI[R]): fs2.Stream[F, PNGraph[R]] = /(rel)

