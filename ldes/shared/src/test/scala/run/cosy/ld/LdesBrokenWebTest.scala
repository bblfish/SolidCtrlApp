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

package run.cosy.ld

import cats.effect.IO
import cats.effect.kernel.{Concurrent, Ref}
import io.lemonlabs as ll
import munit.CatsEffectSuite
import org.w3.banana.{Ops, RDF}
import run.cosy.ld.ldes.{BrokenMiniLdesWWW, MiniLdesWWW}

import scala.collection.immutable.{Seq, Set}

trait LdesBrokenWebTest[R <: RDF]()(using ops: Ops[R]) extends CatsEffectSuite:
   import MiniLdesWWW.*
   val miniWeb = new BrokenMiniLdesWWW[R]
   given www: Web[IO, R] = miniWeb
   import miniWeb.{sosa, tree, wgs84}
   import ops.{*, given}
   import run.cosy.ld.PNGraph.*

   test("get circular data with links going nowhere without breaking") {
     import cats.syntax.traverse.{*, given}
     val z: IO[fs2.Stream[IO, RDF.Graph[R]]] =
       for
          visitedRef <- Ref.of[IO, Set[RDF.URI[R]]](Set())
          views <- www.getPNG(URI(Collection)).map(_.rel(tree.view))
       yield fs2.Stream.unfoldLoopEval(views) { views =>
          import cats.syntax.traverse.{*, given}
          for
             v <- visitedRef.get
             // here we make sure we don't visit the same page twice and we don't fail on missing pages
             pagesEither <- views.collect {
               case ung: UriNGraph[R] if !v.contains(ung.point.fragmentLess) => ung.jump[IO].attempt
             }.sequence
             pages = pagesEither.collect { case Right(png) => png }
             _ <- visitedRef.update { v =>
                val urls = pages.map(_.name)
                v.union(urls.toSet)
             }
          yield
             val nextPages: Seq[UriNGraph[R]] = pages.rel(tree.relation)
               .filterType(tree.GreaterThanRelation).rel(tree.node)
               .collect { case ung: UriNGraph[R] => ung }
             // we need to place the pointer on the Collection of each page
             val collInPages: Seq[UriNGraph[R]] =
                val uc = URI(Collection)
                pages.map(ung => new UriNGraph[R](uc, ung.name, ung.graph))
             val obs: RDF.Graph[R] = collInPages.rel(tree.member).map(png =>
               png.collect(
                 rdf.typ,
                 wgs84.location,
                 sosa.hasSimpleResult,
                 sosa.madeBySensor,
                 sosa.observedProperty,
                 sosa.resultTime
               )()
             ).fold(Graph.empty)((g1, g2) => g1 union g2)
             (
               obs,
               if nextPages.isEmpty then None
               else Some(nextPages)
             )
       }

     z.flatMap { graphs =>
       graphs.compile.toList.map { lst =>
          val g: RDF.Graph[R] = lst.fold(Graph.empty)((g1, g2) => g1 union g2)
          val gTrpls: Set[RDF.Triple[R]] = Set(g.triples.toSeq*)
          val expectedTrpls: Seq[RDF.Triple[R]] = miniWeb.obsrvs.iterator
            .map { (uristr, rtriples) =>
               val u = ll.uri.AbsoluteUrl.parse(uristr)
               rtriples.flatten.map((rt: RDF.rTriple[R]) => rt.resolveAgainst(u)._1)
            }.toSeq.flatten
          val expected: Set[RDF.Triple[R]] = Set(expectedTrpls*)
          // we can compare them as sets as we have no blank nodes
          assertEquals(expected.diff(gTrpls), Set.empty)
          assertEquals(gTrpls.size, expected.size)
          assertEquals(gTrpls, expected)
       }
     }
   }
