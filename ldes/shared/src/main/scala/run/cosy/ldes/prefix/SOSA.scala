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

object SOSA:
  def apply[Rdf <: RDF](using ops: Ops[Rdf]) = new SOSA()

class SOSA[Rdf <: RDF](using ops: Ops[Rdf])
    extends PrefixBuilder[Rdf]("tree", ops.URI("http://www.w3.org/ns/sosa/")):

  val FeatureOfInterest = apply("FeatureOfInterest")
  val ObservableProperty = apply("ObservableProperty")
  val ActuatableProperty = apply("ActuatableProperty")
  val Sample = apply("Sample")
  val hasSample = apply("hasSample")
  val isSampleOf = apply("isSampleOf")
  val Platform = apply("Platform")
  val hosts = apply("hosts")
  val isHostedBy = apply("isHostedBy")
  val Procedure = apply("Procedure")
  val Sensor = apply("Sensor")
  val observes = apply("observes")
  val isObservedBy = apply("isObservedBy")
  val Actuator = apply("Actuator")
  val Sampler = apply("Sampler")
  val usedProcedure = apply("usedProcedure")
  val hasFeatureOfInterest = apply("hasFeatureOfInterest")
  val isFeatureOfInterestOf = apply("isFeatureOfInterestOf")
  val Observation = apply("Observation")
  val madeObservation = apply("madeObservation")
  val madeBySensor = apply("madeBySensor")
  val observedProperty = apply("observedProperty")
  val Actuation = apply("Actuation")
  val madeActuation = apply("madeActuation")
  val madeByActuator = apply("madeByActuator")
  val actsOnProperty = apply("actsOnProperty")
  val isActedOnBy = apply("isActedOnBy")
  val Sampling = apply("Sampling")
  val madeSampling = apply("madeSampling")
  val madeBySampler = apply("madeBySampler")
  val Result = apply("Result")
  val hasResult = apply("hasResult")
  val isResultOf = apply("isResultOf")
  val hasSimpleResult = apply("hasSimpleResult")
  val resultTime = apply("resultTime")
  val phenomenonTime = apply("phenomenonTime")
