package run.cosy.ld

import cats.effect.IO
import cats.effect.kernel.Concurrent
import fs2.Chunk
import io.lemonlabs as ll
import munit.CatsEffectSuite
import org.w3.banana.{Ops, RDF}
import run.cosy.ld.ldes.MiniLdesWWW

/** We deal here with simple data that does not have link loops or such problems We will need to
  * work on that in separate tests. Here we just want to test how to get the data
  */
trait LdesSimpleWebTest[R <: RDF]()(using ops: Ops[R]) extends CatsEffectSuite:
  import MiniLdesWWW.*
  val miniWeb = new MiniLdesWWW[R]
  given www: Web[IO, R] = miniWeb
  import miniWeb.{sosa, tree, wgs84}
  import ops.{*, given}
  import run.cosy.ld.PNGraph.*

  test("walk through pages starting from first page") {
    // we start from the container url, and jump to the first page
    val page1_IO: IO[UriNGraph[R]] = www.getPNG(URI(D09_05))

    // nodeStr has to be in the right graph
    def next(node: SubjPNGraph[R]): Seq[PNGraph[R]] =
      node
        .rel(tree.relation)
        .filterType(tree.GreaterThanRelation)
        .rel(tree.node)

    val result: IO[fs2.Stream[IO, String]] =
      for page1 <- page1_IO
      yield
      // this is a bit clumsy but it works
      fs2.Stream(page1.name.value)
        ++ fs2.Stream.unfoldEval(page1) { (node: UriNGraph[R]) =>
          val ns: Seq[PNGraph[R]] = next(node)
          val x: Option[IO[UriNGraph[R]]] = ns.collectFirst { case ui: UriNGraph[R] =>
            ui.jump
          }
          x match
            case None        => IO(None)
            case Some(ioNgr) => ioNgr.map(ngr => Some((ngr.name.value, ngr)))
        }
    result.flatMap(names =>
      names.compile.toList.map { lst =>
        assertEquals(lst.toSet, Set(D09_05, D09_06, D09_07))
      }
    )
  }

  test("walk through pages but starting from Collection") {
    val z: IO[fs2.Stream[IO, String]] =
      for views <- www.getPNG(URI(Collection)).map(_.rel(tree.view))
      yield fs2.Stream
        .unfoldLoopEval(views) { (views: Seq[PNGraph[R]]) =>
          import cats.syntax.traverse.{*, given}
          val x: Seq[IO[UriNGraph[R]]] =
            // because we are using unfoldLoopEval we need to work with the IO effect, not Streams
            // so we cannot just use views.jump
            views.collect { case ung: UriNGraph[R] => ung.jump }
          val y: IO[Seq[UriNGraph[R]]] = x.sequence
          val res: IO[(Chunk[String], Option[Seq[UriNGraph[R]]])] = y.map { seqUng =>
            val s: Seq[UriNGraph[R]] = seqUng
              .rel(tree.relation)
              .filterType(tree.GreaterThanRelation)
              .rel(tree.node)
              .collect { case ung: UriNGraph[R] => ung }
            (
              Chunk.seq(views.collect { case p: UriNGraph[R] => p.point.value }),
              if s.isEmpty then None
              else Some(s)
            )
          }
          res
        }
        .unchunks
    z.flatMap { urls =>
      urls.compile.toList.map { lst =>
        assertEquals(lst.toSet, Set(D09_05, D09_06, D09_07))
      }
    }
  }

  test("walk through pages and collect observations") {
    val z: IO[fs2.Stream[IO, RDF.Graph[R]]] =
      for views <- www.getPNG(URI(Collection)).map(_.rel(tree.view))
      yield fs2.Stream
        .unfoldLoopEval(views) { (views: Seq[PNGraph[R]]) =>
          import cats.syntax.traverse.{*, given}
          val x: Seq[IO[UriNGraph[R]]] =
            views.collect { case ung: UriNGraph[R] => ung.jump }
          //note: a problem with x.sequence is that it would need all UriNGraphs to be complete
          //before the IO is complete. What if some get stuck? 
          val pagesIO: IO[Seq[UriNGraph[R]]] = x.sequence
//            val pagesIO: IO[Seq[UriNGraph[R]]] =
//              views.collect({ case ung: UriNGraph[R] => ung.jump }).sequence
          val res: IO[(RDF.Graph[R], Option[Seq[UriNGraph[R]]])] = pagesIO.map { seqUng =>
            val nextPages: Seq[UriNGraph[R]] =
              seqUng
                .rel(tree.relation)
                .filterType(tree.GreaterThanRelation)
                .rel(tree.node)
                .collect { case ung: UriNGraph[R] => ung }
            // we need to place the pointer on the Collection of each page
            val collInPages: Seq[UriNGraph[R]] =
              val uc = URI(Collection)
              seqUng.map(ung => new UriNGraph[R](uc, ung.name, ung.graph))
            val obs = collInPages
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
          res
        }
    z.flatMap { graphs =>
      graphs.compile.toList.map { lst =>
        val g: RDF.Graph[R] = lst.fold(Graph.empty)((g1, g2) => g1 union g2)
        val gTrpls: Set[RDF.Triple[R]] = Set(g.triples.toSeq*)
        val expectedTrpls: Seq[RDF.Triple[R]] = miniWeb.obsrvs.iterator
          .map { (uristr, rtriples) =>
            val u = ll.uri.AbsoluteUrl.parse(uristr)
            rtriples.flatten.map((rt: RDF.rTriple[R]) => rt.resolveAgainst(u)._1)
          }
          .toSeq
          .flatten
        val expected: Set[RDF.Triple[R]] = Set(expectedTrpls*)
        // we can compare them as sets as we have no blank nodes
        assertEquals(expected.diff(gTrpls), Set.empty)
        assertEquals(gTrpls.size, expected.size)
        assertEquals(gTrpls, expected)
      }
    }
  }
