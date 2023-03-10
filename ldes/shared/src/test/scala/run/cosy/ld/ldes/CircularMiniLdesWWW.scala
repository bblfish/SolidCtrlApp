package run.cosy.ld.ldes

import cats.effect.IO
import org.w3.banana.*
import org.w3.banana.diesel.{*, given}

class CircularMiniLdesWWW[R <: RDF](using ops: Ops[R]) extends MiniLdesWWW[R]:
  import MiniLdesWWW.*
  import ops.{*, given}

  // we introduce a circularity in the pagination of D09_07
  override def getRelativeGraph(url: RDF.URI[R]): RDF.rGraph[R] =
    import scala.language.implicitConversions
    url.value match
      case D09_07 =>
        (rURI("").a(tree.Node)
          -- tree.relation ->- (
            BNode().a(tree.LessThanRelation)
              -- tree.node ->- rURI("2021-09-07")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
          )
          -- tree.relation ->- (
            BNode().a(tree.GreaterThanRelation)
              -- tree.node ->- rURI("2021-09-05") //warning: error circularity
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
      case _ => super.getRelativeGraph(url)
