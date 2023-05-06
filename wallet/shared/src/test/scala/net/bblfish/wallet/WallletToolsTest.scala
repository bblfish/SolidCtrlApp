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

import org.w3.banana.{Ops, RDF}

import scala.util.Success
import scala.language.implicitConversions
import io.lemonlabs.uri as ll
import org.http4s.Method.GET
import org.w3.banana.RDF.Statement as St

trait WalletToolsTest[R <: RDF](using ops: Ops[R]) extends munit.FunSuite {
  val wt = new WalletTools[R]
  import ops.{*, given}
  import wt.*
  import org.w3.banana.diesel.{*, given}

  val acl1: RDF.rGraph[R] = (rURI("#R0") -- rdf.typ ->- wac.Authorization
    -- wac.mode ->- wac.Control
    -- wac.agent ->- BNode("a")).graph ++ (
    rURI("#R1") -- rdf.`type` ->- wac.Authorization
      -- wac.mode ->- wac.Read
      -- wac.default ->- rURI(".")
      -- wac.agent ->- rURI("#a") // we use a URI here for testAgents
  ).graph.triples.toSeq ++ (
    rURI("#R2") -- rdf.`type` ->- wac.Authorization
      -- wac.mode ->- wac.Write
      -- wac.default ->- rURI(".")
      -- wac.agent ->- rURI("#a")
  ).graph.triples.toSeq ++ (
    rURI("/rfcKey#") -- sec.controller ->- wac.Write
  ).graph.triples.toSeq

  val bbl = URI("https://bblfish.net/")
  val bblPpl = URI("https://bblfish.net/people")
  val bblPplS = URI("https://bblfish.net/people/")
  val bblCard = URI("https://bblfish.net/people/henry/card")
  val alice = URI("https://alice.name/card#me")

  test("WalletTools.within") {
    assertEquals(wt.withinTry(bblCard, bbl), Success(true))
    assertEquals(wt.withinTry(bbl, bblCard), Success(false))
    assertEquals(wt.withinTry(bblCard, bblPpl), Success(true))
    assertEquals(wt.withinTry(bblCard, bblPplS), Success(true))

    assertEquals(bbl.contains(bblPpl), true)
    assertEquals(bbl.contains(bblPplS), true)
    assertEquals(bblPplS.contains(bblPplS), true)
    assertEquals(bblPplS.contains(bblCard), true)
  }

  val bblRootAcl = ll.AbsoluteUrl.parse("https://bblfish.net/.acl")
  val acl1Gr: RDF.Graph[R] = acl1.resolveAgainst(bblRootAcl)

  test("test find acl") {
    val r1r2: Set[St.Subject[R]] =
      Set(URI("https://bblfish.net/.acl#R1"), URI("https://bblfish.net/.acl#R2"))

    val n1: Set[St.Subject[R]] = findAclsFor(acl1Gr, bbl).toSet
    assertEquals(n1, r1r2)

    val n2: Set[St.Subject[R]] = findAclsFor(acl1Gr, bblPplS).toSet
    assertEquals(n2, r1r2)

    val n3: Set[St.Subject[R]] = findAclsFor(acl1Gr, bblCard).toSet
    assertEquals(n3, r1r2)

    val n4: List[St.Subject[R]] = findAclsFor(acl1Gr, alice).toList
    assertEquals(n4, List())

    // todo: add code for the wac:
  }

  test("findAgents") {
    assertEquals(findAgents(acl1Gr, bblCard, GET).toList, List(URI("https://bblfish.net/.acl#a")))

  }

}
