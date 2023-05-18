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
import cats.effect.kernel.Concurrent
import org.w3.banana.RDF
import org.w3.banana.Ops
import munit.CatsEffectSuite
import run.cosy.ld.MiniFoafWWW.EricP

trait FoafWebTest[R <: RDF]()(using ops: Ops[R]) extends CatsEffectSuite:
   val miniWeb = new MiniFoafWWW[R]
   import miniWeb.foaf
   given www: Web[IO, R] = miniWeb
   import ops.{*, given}
   import PNGraph.{*, given}
   import MiniFoafWWW.*
   import cats.effect.IO.asyncForIO

   test("find bbl friends WebIDs inside graph (no jump)") {

     val bblng: IO[UriNGraph[R]] = www.getPNG(URI(Bbl))
     bblng.map { bblNg =>
        // todo: make it possible to just get nodes
        val kns: Seq[String] = (bblNg / foaf.knows).collect { case u: UriNGraph[R] =>
          u.point.value
        }
        assertEquals(Set(kns*), Set(EricP, Timbl, CSarven))
     }

   }

   test("find bblFriend WebIDs after jumping") {
     www.getPNG(URI(Bbl)).flatMap { bblNg =>
        val friendsDef: fs2.Stream[IO, PNGraph[R]] = (bblNg / foaf.knows).jump
        val expectedURIs: Set[String] = Set(EricP, Timbl, CSarven)
        val result1: IO[Set[String]] = friendsDef.compile.toList.map(pnglst =>
           val uris: Seq[String] = pnglst.collect { case u: UriNGraph[R] => u.point.value }
           Set(uris*)
        )
        result1.map(set => assertEquals(set, expectedURIs))
     }

     // todo: test what happens if a WebID is broken.
   }

   test("find canonical names of friends (as defined in their profile)") {
     www.getPNG(URI(Bbl)).flatMap { bblNg =>
        // most of the names are only available from the definitional graphs
        val names: fs2.Stream[IO, String] = (bblNg / foaf.knows).jump
          .collect { case ug: UriNGraph[R] => fs2.Stream(ug / foaf.name*) }.flatten
          .collect { case litG: LiteralNGraph[R] => litG.point.text }
        names.compile.toList.map(lst =>
          assertEquals(Set(lst*), Set("Tim Berners-Lee", "Eric Prud'hommeaux", "Sarven Capadisli"))
        )
     }
   }

   test("find all names of friends (local and remote)") {
     www.getPNG(URI(Bbl)).flatMap { bblNg =>
        // most of the names are only available from the definitional graphs
        val allNames: fs2.Stream[IO, String] = (bblNg / foaf.knows).jump
          .collect { case ug: SubjPNGraph[R] => fs2.Stream(ug / foaf.name*) }.flatten
          .collect { case litG: LiteralNGraph[R] => litG.point.text }

        allNames.compile.toList.map { lst =>
          assertEquals(
            Set(lst*),
            Set("Tim Berners-Lee", "Eric Prud'hommeaux", "Sarven Capadisli", "James Gosling")
          )
        }
     }
   }

   test("find all names of friends (local and remote) - shorter version") {
     www.getPNG(URI(Bbl)).flatMap { bblNg =>
        // most of the names are only available from the definitional graphs
        val allNames: fs2.Stream[IO, String] = bblNg.rel(foaf.knows).jump.rel(foaf.name)
          .collect { case litG: LiteralNGraph[R] => litG.point.text }

        allNames.compile.toList.map { lst =>
          assertEquals(
            Set(lst*),
            Set("Tim Berners-Lee", "Eric Prud'hommeaux", "Sarven Capadisli", "James Gosling")
          )
        }
     }
   }
