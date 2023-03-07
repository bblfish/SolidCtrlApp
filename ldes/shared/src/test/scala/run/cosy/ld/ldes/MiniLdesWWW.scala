package run.cosy.ld.ldes

import cats.effect.IO
import io.lemonlabs.uri.AbsoluteUrl
import org.w3.banana.diesel.{*, given}
import org.w3.banana.{diesel, *}
import run.cosy.ld.*
import run.cosy.ld.ldes.prefix as ldesPre

object MiniLdesWWW:
  def mechelen(date: String): String = "https://ldes.mechelen.org/" + date
  val D09_05: String = mechelen("2021-09-05")
  val D09_06: String = mechelen("2021-09-06")
  val D09_07: String = mechelen("2021-09-07")
  val Container: String = mechelen("")

class MiniLdesWWW[R <: RDF](using ops: Ops[R]) extends Web[IO, R]:
  import MiniLdesWWW.*
  import ops.{*, given}
  val foaf = prefix.FOAF[R]
  val tree = ldesPre.TREE[R]
  val sosa = ldesPre.SOSA[R]
  val wgs84 = ldesPre.WGS84[R]
  val ldes = ldesPre.LDES[R]

  def area(loc: String) = rURI("area#d" + loc)
  def pzDev(num: String) = URI("https://data.politie.be/sensor/dev#" + num)
  def polMsr(tp: String) = URI("https://data.politie.be/sensors/measurement#" + tp)
  def crop(area: String) = URI("https://data.cropland.be/area#" + area)
  def cropProp(prop: String) = URI("https://data.cropland.be/measure#" + prop)

  def getPNG(url: RDF.URI[R]): IO[UriNGraph[R]] =
    val doc = url.fragmentLess
    get(doc).map(g => new UriNGraph(url, doc, g))

  def get(url: RDF.URI[R]): IO[RDF.Graph[R]] =
    import scala.language.implicitConversions
    val res: RDF.rGraph[R] =
      url.value match
        case Container =>
          (rURI("").a(ldes.EventStream)
            -- ldes.timestampPath ->- sosa.resultTime
            -- tree.shape ->- rURI("flows-shacl")
            -- tree.view ->- rURI("2021-09-05")
          ).graph
        case D09_05 =>
          (rURI("") -- rdf.typ ->- tree.Node
            -- tree.relation ->- (
              BNode() -- rdf.typ ->- tree.GreaterThanRelation
                -- tree.node ->- rURI("2021-09-06")
                -- tree.path ->- sosa.resultTime
                -- tree.value ->- ("2021-09-06T00:00:00+02" ^^ xsd.dateTimeStamp)
            )).graph ++ (
            rURI("#3") -- rdf.typ ->- sosa.Observation
              -- wgs84.location ->- area("loc781089")
              -- sosa.hasSimpleResult ->- ("4.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213501")
              -- sosa.observedProperty ->- polMsr("motorized")
              -- sosa.resultTime ->- ("2021-09-05T23:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#482") -- rdf.typ ->- sosa.Observation
              -- wgs84.location ->- area("loc")
              -- sosa.hasSimpleResult ->- ("2455.1123" ^^ xsd.float)
              -- sosa.madeBySensor ->- crop("schoolstraat")
              -- sosa.observedProperty ->- cropProp("deviceNbr")
              -- sosa.resultTime ->- ("2021-09-05T22:30:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#4464") -- rdf.typ ->- sosa.Observation
              -- wgs84.location ->- area("loc734383")
              -- sosa.hasSimpleResult ->- ("10.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213504+5+6")
              -- sosa.observedProperty ->- polMsr("bike")
              -- sosa.resultTime ->- ("2021-09-05T23:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
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
            )).graph ++ (
            rURI("#3003").a(sosa.Observation)
              -- wgs84.location ->- area("loc763628")
              -- sosa.hasSimpleResult ->- ("44.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213503")
              -- sosa.observedProperty ->- polMsr("motorized")
              -- sosa.resultTime ->- ("2021-09-06T11:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#4493").a(sosa.Observation)
              -- wgs84.location ->- area("loc734383")
              -- sosa.hasSimpleResult ->- ("197.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213504+5+6")
              -- sosa.observedProperty ->- polMsr("motorized")
              -- sosa.resultTime ->- ("2021-09-06T12:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#48").a(sosa.Observation)
              -- wgs84.location ->- area("loc781089")
              -- sosa.hasSimpleResult ->- ("1.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213501")
              -- sosa.observedProperty ->- polMsr("bike")
              -- sosa.resultTime ->- ("2021-09-06T22:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
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
                -- tree.node ->- rURI("2021-09-07")
                -- tree.path ->- sosa.resultTime
                -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
            )).graph ++ (
            rURI("#658").a(sosa.Observation)
              -- wgs84.location ->- area("loc")
              -- sosa.hasSimpleResult ->- ("5087.4795" ^^ xsd.float)
              -- sosa.madeBySensor ->- crop("schoolstraat")
              -- sosa.observedProperty ->- cropProp("deviceNbr")
              -- sosa.resultTime ->- ("2021-09-07T18:30:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#637").a(sosa.Observation)
              -- wgs84.location ->- area("loc")
              -- sosa.hasSimpleResult ->- ("7009.3345" ^^ xsd.float)
              -- sosa.madeBySensor ->- crop("schoolstraat")
              -- sosa.observedProperty ->- cropProp("deviceNbr")
              -- sosa.resultTime ->- ("2021-09-07T13:15:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI("#3074").a(sosa.Observation)
              -- wgs84.location ->- area("loc763628")
              -- sosa.hasSimpleResult ->- ("1.0" ^^ xsd.float)
              -- sosa.madeBySensor ->- pzDev("213503")
              -- sosa.observedProperty ->- polMsr("bike")
              -- sosa.resultTime ->- ("2021-09-06T22:00:00+02" ^^ xsd.dateTimeStamp)
          ).graph.triples.toSeq ++ (
            rURI(".").a(ldes.EventStream)
              -- ldes.timestampPath ->- sosa.resultTime
              -- tree.shape ->- rURI("flows-shacl")
              -- tree.view ->- rURI("")
              -- tree.member ->- rURI("#658")
              -- tree.member ->- rURI("#3074")
              -- tree.member ->- rURI("#637")
          ).graph.triples.toSeq
    IO(res.resolveAgainst(AbsoluteUrl.parse(url.value)))
