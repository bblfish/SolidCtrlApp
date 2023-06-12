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

package run.cosy.ldes.prefix

import org.w3.banana.{Ops, PrefixBuilder, RDF}

object WGS84:
   def apply[R <: RDF](using Ops[R]) = new WGS84()

class WGS84[R <: RDF](using ops: Ops[R])
    extends PrefixBuilder[R]("wgs84", ops.URI("http://www.w3.org/2003/01/geo/wgs84_pos#")):

   lazy val Point = apply("Point")
   lazy val SpatialThing = apply("SpatialThing")
   lazy val alt = apply("alt")
   lazy val lat = apply("lat")
   lazy val lat_long = apply("lat_long")
   lazy val location = apply("location")
