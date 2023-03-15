package run.cosy.ldes.prefix

import org.w3.banana.{Ops, PrefixBuilder, RDF}

object LDES:
  def apply[Rdf <: RDF](using ops: Ops[Rdf]) = new LDES()

class LDES[Rdf <: RDF](using ops: Ops[Rdf])
  extends PrefixBuilder[Rdf]("ldes", ops.URI("https://w3id.org/ldes#")):

  val EventStream = apply("EventStream")
  val EventSource = apply("EventSource")
  val RetentionPolicy = apply("RetentionPolicy")
  val LatestVersionSubset = apply("LatestVersionSubset")
  val DurationAgoPolicy = apply("DurationAgoPolicy")
  val retentionPolicy = apply("retentionPolicy")
  val amount = apply("amount")
  val versionKey = apply("versionKey")
  val versionOfPath = apply("versionOfPath")
  val timestampPath = apply("timestampPath")
  val versionMaterializationOf = apply("versionMaterializationOf")
  val versionMaterializationUntil = apply("versionMaterializationUntil")

