package run.cosy.ld.ldes

import cats.effect.IO
import org.w3.banana.*
import org.w3.banana.diesel.{*, given}

/** Ldes example with circularity and link pointing nowhere */
class BrokenMiniLdesWWW[R <: RDF](using ops: Ops[R]) extends MiniLdesWWW[R]:
  import MiniLdesWWW.*
  import ops.{*, given}

  // we introduce a circularity in the pagination of D09_07
  override def getRelativeGraph(url: RDF.URI[R]): Option[RDF.rGraph[R]] =
    import scala.language.implicitConversions
    url.value match
      case D09_07 =>
        val g: RDF.rGraph[R] = (rURI("").a(tree.Node)
          -- tree.relation ->- (
            BNode().a(tree.LessThanRelation)
              -- tree.node ->- rURI("2021-09-07")
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
          )
          -- tree.relation ->- (
            BNode().a(tree.GreaterThanRelation)
              -- tree.node ->- rURI("2021-09-09") // warning: does not exist
              -- tree.path ->- sosa.resultTime
              -- tree.value ->- ("2021-09-07T00:00:00+02" ^^ xsd.dateTimeStamp)
          )
          -- tree.relation ->- (
            BNode().a(tree.GreaterThanRelation)
              -- tree.node ->- rURI("2021-09-05") // warning: error circularity
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
        Some(g)
      case Collection =>
         // we add a link to a non existing view
         super.getRelativeGraph(url).map{rg =>
            rg + rTriple(rURI(""), tree.view, rURI("2021-09-20"))
         }
      case _ => super.getRelativeGraph(url)
