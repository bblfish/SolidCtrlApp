package run.cosy.ld

import cats.effect.IO
import cats.effect.kernel.{Concurrent, Ref}
import io.lemonlabs as ll
import munit.CatsEffectSuite
import org.w3.banana.{Ops, RDF}
import run.cosy.ld.ldes.{CircularMiniLdesWWW, MiniLdesWWW}

import scala.collection.immutable.{Seq, Set}

trait LdesCircularWebTest[R <: RDF]()(using ops: Ops[R]) extends CatsEffectSuite:
  import MiniLdesWWW.*
  val miniWeb = new CircularMiniLdesWWW[R]
  given www: Web[IO, R] = miniWeb
  import miniWeb.{sosa, tree, wgs84}
  import ops.{*, given}
  import run.cosy.ld.PNGraph.*

  test("get circular data but recognise circularity") {
    import cats.syntax.traverse.{*, given}
    val z: IO[fs2.Stream[IO, RDF.Graph[R]]] = {
      for {
        visitedRef <- Ref.of[IO, Set[RDF.URI[R]]](Set())
        views <- www.getPNG(URI(Collection)).map(_.rel(tree.view))
      } yield {
        fs2.Stream
          .unfoldLoopEval(views) { views =>
            import cats.syntax.traverse.{*, given}
            for
              v <- visitedRef.get
              // here we make sure we don't visit the same page twice.
              pages <- views.collect {
                   case ung: UriNGraph[R] if !v.contains(ung.point.fragmentLess) => ung.jump[IO]
                 }.sequence
              _ <- visitedRef.update { v =>
                val urls = pages.map(_.name)
                v.union(urls.toSet)
              }
            yield {
              val nextPages: Seq[UriNGraph[R]] = pages
                .asInstanceOf[Seq[UriNGraph[R]]]
                .rel(tree.relation)
                .filterType(tree.GreaterThanRelation)
                .rel(tree.node)
                .collect { case ung: UriNGraph[R] => ung }
              // we need to place the pointer on the Collection of each page
              val collInPages: Seq[UriNGraph[R]] =
                val uc = URI(Collection)
                pages.map(ung => new UriNGraph[R](uc, ung.name, ung.graph))
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

          }
      }
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
