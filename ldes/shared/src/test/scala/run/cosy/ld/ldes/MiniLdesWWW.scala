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

package run.cosy.ld.ldes

import cats.effect.IO
import io.lemonlabs.uri.AbsoluteUrl
import org.w3.banana.diesel.{*, given}
import org.w3.banana.{diesel, *}
import run.cosy.ld.*
import run.cosy.ldes.prefix as ldesPre
import run.cosy.ldes.prefix.{LDES, SOSA, TREE, WGS84}

import scala.language.implicitConversions

object MiniLdesWWW:
   def mechelen(date: String): String = "https://ldes.mechelen.org/" + date
   val D09_05: String = mechelen("2021-09-05")
   val D09_06: String = mechelen("2021-09-06")
   val D09_07: String = mechelen("2021-09-07")
   val Collection: String = mechelen("")

class MiniLdesWWW[R <: RDF](using ops: Ops[R]) extends Web[IO, R]:
   import MiniLdesWWW.*
   import ops.{*, given}
   val foaf = prefix.FOAF[R]
   val tree = TREE[R]
   val sosa = SOSA[R]
   val wgs84 = WGS84[R]
   val ldes = LDES[R]

   def area(loc: String) = rURI("area#d" + loc)
   def pzDev(num: String) = URI("https://data.politie.be/sensor/dev#" + num)
   def polMsr(tp: String) = URI("https://data.politie.be/sensors/measurement#" + tp)
   def crop(area: String) = URI("https://data.cropland.be/area#" + area)
   def cropProp(prop: String) = URI("https://data.cropland.be/measure#" + prop)

   def get(url: RDF.URI[R]): IO[RDF.Graph[R]] = getRelativeGraph(url) match
    case Some(res) => IO(res.resolveAgainst(AbsoluteUrl.parse(url.value)))
    case None => IO.raiseError[RDF.Graph[R]](new Exception(s"resource $url not reachable"))

   def getPNG(url: RDF.URI[R]): IO[UriNGraph[R]] =
      val doc = url.fragmentLess
      get(doc).map(g => new UriNGraph(url, doc, g))

   def observation(
       name: String,
       loc: String,
       simpleResult: String,
       sensor: RDF.URI[R],
       observedProp: RDF.URI[R],
       resultTime: String
   ): Seq[RDF.rTriple[R]] = (rURI("#" + name) -- rdf.typ ->- sosa.Observation
     -- wgs84.location ->- area(loc)
     -- sosa.hasSimpleResult ->- (simpleResult ^^ xsd.float)
     -- sosa.madeBySensor ->- sensor
     -- sosa.observedProperty ->- observedProp
     -- sosa.resultTime ->- (resultTime ^^ xsd.dateTimeStamp)).graph.triples.toSeq

   val obsrvs: Map[String, Seq[Seq[RDF.rTriple[R]]]] = Map(
     D09_05 -> Seq(
       observation(
         "3",
         "loc781089",
         "4.0",
         pzDev("213501"),
         polMsr("motorized"),
         "2021-09-05T23:00:00+02"
       ),
       observation(
         "482",
         "loc",
         "2455.1123",
         crop("schoolstraat"),
         cropProp("deviceNbr"),
         "2021-09-05T22:30:00+02"
       ),
       observation(
         "4464",
         "loc734383",
         "10.0",
         pzDev("213504+5+6"),
         polMsr("bike"),
         "2021-09-05T23:00:00+02"
       )
     ),
     D09_06 -> Seq(
       observation(
         "3003",
         "loc763628",
         "44.0",
         pzDev("213503"),
         polMsr("motorized"),
         "2021-09-06T11:00:00+02"
       ),
       observation(
         "4493",
         "loc734383",
         "197.0",
         pzDev("213504+5+6"),
         polMsr("motorized"),
         "2021-09-06T12:00:00+02"
       ),
       observation(
         "48",
         "loc781089",
         "1.0",
         pzDev("213501"),
         polMsr("bike"),
         "2021-09-06T22:00:00+02"
       )
     ),
     D09_07 -> Seq(
       observation(
         "658",
         "loc",
         "5087.4795",
         crop("schoolstraat"),
         cropProp("deviceNbr"),
         "2021-09-07T18:30:00+02"
       ),
       observation(
         "637",
         "loc",
         "7009.3345",
         crop("schoolstraat"),
         cropProp("deviceNbr"),
         "2021-09-07T13:15:00+02"
       ),
       observation(
         "3074",
         "loc763628",
         "1.0",
         pzDev("213503"),
         polMsr("bike"),
         "2021-09-06T22:00:00+02"
       )
     )
   )

   def getRelativeGraph(url: RDF.URI[R]): Option[RDF.rGraph[R]] = relG.lift(url.value)

   def relG: PartialFunction[String, RDF.rGraph[R]] =
      case Collection => (rURI("").a(ldes.EventStream)
          -- ldes.timestampPath ->- sosa.resultTime
          -- tree.shape ->- rURI("flows-shacl")
          -- tree.view ->- rURI("2021-09-05")).graph
      case D09_05 =>
        (rURI("") -- rdf.typ ->- tree.Node
          -- tree.relation ->- (
            BNode() -- rdf.typ ->- tree.GreaterThanRelation
              -- tree.node ->- rURI("2021-09-06")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-06T00:00:00+02" ^^ xsd.dateTimeStamp)
          )).graph ++ obsrvs(D09_05).flatten ++ (
          rURI(".").a(ldes.EventStream)
            -- ldes.timestampPath ->- sosa.resultTime
            -- tree.shape ->- rURI("flows-shacl")
            -- tree.view ->- rURI("")
            -- tree.member ->- rURI("#3")
            -- tree.member ->- rURI("#482")
            -- tree.member ->- rURI("#4464")
        ).graph.triples.toSeq
      case D09_06 =>
        (rURI("").a(tree.Node)
          -- tree.relation ->- (
            BNode().a(tree.LessThanRelation)
              -- tree.node ->- rURI("2021-09-05")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-06T00:00:00+02" ^^ xsd.dateTimeStamp)
          )
          -- tree.relation ->- (
            BNode().a(tree.GreaterThanRelation)
              -- tree.node ->- rURI("2021-09-07")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
          )).graph ++ obsrvs(D09_06).flatten ++ (
          rURI(".").a(ldes.EventStream)
            -- ldes.timestampPath ->- sosa.resultTime
            -- tree.shape ->- rURI("flows-shacl")
            -- tree.view ->- rURI("")
            -- tree.member ->- rURI("#4493")
            -- tree.member ->- rURI("#48")
            -- tree.member ->- rURI("#3003")
        ).graph.triples.toSeq
      case D09_07 =>
        (rURI("").a(tree.Node)
          -- tree.relation ->- (
            BNode().a(tree.LessThanRelation)
              -- tree.node ->- rURI("2021-09-06")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
          )).graph ++ obsrvs(D09_07).flatten ++ (
          rURI(".").a(ldes.EventStream)
            -- ldes.timestampPath ->- sosa.resultTime
            -- tree.shape ->- rURI("flows-shacl")
            -- tree.view ->- rURI("")
            -- tree.member ->- rURI("#658")
            -- tree.member ->- rURI("#3074")
            -- tree.member ->- rURI("#637")
        ).graph.triples.toSeq
