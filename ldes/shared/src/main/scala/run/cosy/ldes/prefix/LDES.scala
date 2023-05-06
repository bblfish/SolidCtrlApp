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
