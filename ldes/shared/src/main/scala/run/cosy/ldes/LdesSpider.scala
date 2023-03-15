package run.cosy.ldes

import cats.effect.{IO, Ref}
import cats.effect.kernel.Concurrent
import org.w3.banana.RDF.{Graph, Node, Statement, URI}
import org.w3.banana.{Ops, RDF}
import run.cosy.ld.{PNGraph, UriNGraph, Web}

import scala.collection.immutable.Set

class LdesSpider[F[_]: Concurrent, R <: RDF](
  using www: Web[F, R],
  ops: Ops[R]
):

  import ops.{*, given}
  import org.w3.banana.prefix
  import run.cosy.ldes.prefix as ldesPre

  val foaf = prefix.FOAF[R]
  val tree = ldesPre.TREE[R]
  val sosa = ldesPre.SOSA[R]
  val wgs84 = ldesPre.WGS84[R]
  val ldes = ldesPre.LDES[R]
  
  
  /** given the ldes stream URL, crawl the nodes of that stream  */
  def crawl(stream: RDF.URI[R]): F[fs2.Stream[F, RDF.Graph[R]]] =
    import cats.syntax.all.toFunctorOps
    import cats.syntax.flatMap.toFlatMapOps
    for
      visitedRef <- Ref.of[F, Set[RDF.URI[R]]](Set())
      views <- www.getPNG(stream).map(_.rel(tree.view))
    yield crawlNodesForward(stream, views, visitedRef)

  /* This crawls pages forward and collects all tree.member observations...
   * This is very much tied to a particular use of ldes
   * todo: it should be able to fetch the shEx to find out what to
   *   collect, or an pattern object should be passed.
   * @returns an fs2.Stream of such observation mini graphs
   **/
  def crawlNodesForward(
      stream: RDF.URI[R],
      startNodes: Seq[PNGraph[R]],
      visitedRef: Ref[F, Set[RDF.URI[R]]]
  ): fs2.Stream[F, RDF.Graph[R]] =
    import cats.syntax.all.toFlatMapOps
    fs2.Stream.unfoldLoopEval(startNodes) { nodes =>
      import cats.syntax.traverse.{*, given}
      import cats.syntax.all.toFunctorOps
      for
        v <- visitedRef.get
        // here we make sure we don't visit the same page twice and we don't fail on missing pages
        pagesEither <- nodes.collect {
          case ung: UriNGraph[R] if !v.contains(ung.point.fragmentLess) =>
            import cats.implicits.catsSyntaxApplicativeError
            ung.jump[F].attempt
        }.sequence
        pages: Seq[UriNGraph[R]] = pagesEither.collect { case Right(png) => png }
        _ <- visitedRef.update { v =>
          val urls = pages.map(_.name)
          v.union(urls.toSet)
        }
      yield
        val nextPages: Seq[UriNGraph[R]] = pages
          .rel(tree.relation)
          .filterType(tree.GreaterThanRelation)
          .rel(tree.node)
          .collect { case ung: UriNGraph[R] => ung }
        // we need to place the pointer on the Collection of each page
        val collInPages: Seq[UriNGraph[R]] =
          pages.map(ung => new UriNGraph[R](stream, ung.name, ung.graph))
        val obs: RDF.Graph[R] = collInPages
          .rel(tree.member)
          .map(png =>
            png.collect(
              rdf.typ,
              wgs84.location,
              sosa.hasSimpleResult,
              sosa.madeBySensor,
              sosa.observedProperty,
              sosa.resultTime
            )()
          )
          .fold(Graph.empty)((g1, g2) => g1 union g2)
        (
          obs,
          if nextPages.isEmpty then None
          else Some(nextPages)
        )
    }
  end crawlNodesForward
  
