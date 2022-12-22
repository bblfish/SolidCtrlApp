package net.bblfish.web.util

import org.w3.banana.{Ops, PrefixBuilder, RDF}


object SecurityPrefix:
  def apply[Rdf <: RDF](using ops: Ops[Rdf]) = new SecurityPrefix[Rdf]

/**
  * Note:  the security prefix https://w3id.org/security/v1# is not a namespace!
  * That is a context document for rdfa, containing shortcuts for many different names
  * coming from different namespaces.
  * TODO: we need something like the Prefix class to model these!
  * TODO: we have a copy of this in Reactive Solid. Where should this go?
  *    (actually perhaps somehwere in this repo is not a bad idea)
  * @param ops
  * @tparam Rdf
  */
class SecurityPrefix[Rdf <: RDF](using val ops: Ops[Rdf])
  extends PrefixBuilder[Rdf](
    "security",
    ops.URI("https://w3id.org/security#")
  ):
  
  val controller   = apply("controller")
  val publicKeyJwk = apply("publicKeyJwk")

end SecurityPrefix
