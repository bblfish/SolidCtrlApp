package run.cosy.ld.ldes.prefix

import org.w3.banana.{Ops, PrefixBuilder, RDF}

object TREE:
  def apply[Rdf <: RDF](using ops: Ops[Rdf]) = new TREE()

class TREE[Rdf <: RDF](using ops: Ops[Rdf])
  extends PrefixBuilder[Rdf]("tree", ops.URI("https://w3id.org/tree#")):

  val Collection = apply("Collection")
  val ViewDescription = apply("ViewDescription")
  val Node = apply("Node")
  val Relation = apply("Relation")
  val ConditionalImport = apply("ConditionalImport")
  val PrefixRelation = apply("PrefixRelation")
  val SubstringRelation = apply("SubstringRelation")
  val SuffixRelation = apply("SuffixRelation")
  val GreaterThanRelation = apply("GreaterThanRelation")
  val GreaterThanOrEqualToRelation = apply("GreaterThanOrEqualToRelation")
  val LessThanRelation = apply("LessThanRelation")
  val LessThanOrEqualToRelation = apply("LessThanOrEqualToRelation")
  val EqualToRelation = apply("EqualToRelation")
  val GeospatiallyContainsRelation = apply("GeospatiallyContainsRelation")
  val InBetweenRelation = apply("InBetweenRelation")
  val viewDescription = apply("viewDescription")
  val relation = apply("relation")
  val remainingItems = apply("remainingItems")
  val node = apply("node")
  val value = apply("value")
  val path = apply("path")
  val view = apply("view")
  val member = apply("member")
  val search = apply("search")
  val shape = apply("shape")
  val conditionalImport = apply("conditionalImport")
  val zoom = apply("zoom")
  val longitudeTile = apply("longitudeTile")
  val latitudeTile = apply("latitudeTile")
  val timeQuery = apply("timeQuery")
  
  