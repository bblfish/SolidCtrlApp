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

package net.bblfish.web.util

import org.w3.banana.{Ops, PrefixBuilder, RDF}

object SecurityPrefix:
   def apply[Rdf <: RDF](using ops: Ops[Rdf]) = new SecurityPrefix[Rdf]

/** Note: the security prefix https://w3id.org/security/v1# is not a namespace! That is a context
  * document for rdfa, containing shortcuts for many different names coming from different
  * namespaces. TODO: we need something like the Prefix class to model these! TODO: we have a copy
  * of this in Reactive Solid. Where should this go? (actually perhaps somewhere in this repo is not
  * a bad idea)
  * @param ops
  * @tparam Rdf
  */
class SecurityPrefix[Rdf <: RDF](using val ops: Ops[Rdf])
    extends PrefixBuilder[Rdf](
      "security",
      ops.URI("https://w3id.org/security#")
    ):

   val controller = apply("controller")
   val publicKeyJwk = apply("publicKeyJwk")

end SecurityPrefix
