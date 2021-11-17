package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object Util {
  
  @JSImport("n3", "Util")
  @js.native
  val ^ : js.Any = js.native
  
  @scala.inline
  def inDefaultGraph(value: n3js.rdfjsTypes.dataModelMod.Quad): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("inDefaultGraph")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def isBlankNode(): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isBlankNode")().asInstanceOf[Boolean]
  @scala.inline
  def isBlankNode(value: n3js.rdfjsTypes.dataModelMod.Term): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isBlankNode")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def isDefaultGraph(): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isDefaultGraph")().asInstanceOf[Boolean]
  @scala.inline
  def isDefaultGraph(value: n3js.rdfjsTypes.dataModelMod.Term): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isDefaultGraph")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def isLiteral(): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isLiteral")().asInstanceOf[Boolean]
  @scala.inline
  def isLiteral(value: n3js.rdfjsTypes.dataModelMod.Term): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isLiteral")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def isNamedNode(): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isNamedNode")().asInstanceOf[Boolean]
  @scala.inline
  def isNamedNode(value: n3js.rdfjsTypes.dataModelMod.Term): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isNamedNode")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def isVariable(): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isVariable")().asInstanceOf[Boolean]
  @scala.inline
  def isVariable(value: n3js.rdfjsTypes.dataModelMod.Term): Boolean = ^.asInstanceOf[js.Dynamic].applyDynamic("isVariable")(value.asInstanceOf[js.Any]).asInstanceOf[Boolean]
  
  @scala.inline
  def prefix(iri: String): PrefixedToIri = ^.asInstanceOf[js.Dynamic].applyDynamic("prefix")(iri.asInstanceOf[js.Any]).asInstanceOf[PrefixedToIri]
  @scala.inline
  def prefix(
    iri: String,
    factory: n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
  ): PrefixedToIri = (^.asInstanceOf[js.Dynamic].applyDynamic("prefix")(iri.asInstanceOf[js.Any], factory.asInstanceOf[js.Any])).asInstanceOf[PrefixedToIri]
  @scala.inline
  def prefix(iri: n3js.rdfjsTypes.dataModelMod.NamedNode[String]): PrefixedToIri = ^.asInstanceOf[js.Dynamic].applyDynamic("prefix")(iri.asInstanceOf[js.Any]).asInstanceOf[PrefixedToIri]
  @scala.inline
  def prefix(
    iri: n3js.rdfjsTypes.dataModelMod.NamedNode[String],
    factory: n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
  ): PrefixedToIri = (^.asInstanceOf[js.Dynamic].applyDynamic("prefix")(iri.asInstanceOf[js.Any], factory.asInstanceOf[js.Any])).asInstanceOf[PrefixedToIri]
  
  @scala.inline
  def prefixes(defaultPrefixes: Prefixes[n3js.rdfjsTypes.dataModelMod.NamedNode[String] | String]): js.Function1[/* prefix */ String, PrefixedToIri] = ^.asInstanceOf[js.Dynamic].applyDynamic("prefixes")(defaultPrefixes.asInstanceOf[js.Any]).asInstanceOf[js.Function1[/* prefix */ String, PrefixedToIri]]
  @scala.inline
  def prefixes(
    defaultPrefixes: Prefixes[n3js.rdfjsTypes.dataModelMod.NamedNode[String] | String],
    factory: n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
  ): js.Function1[/* prefix */ String, PrefixedToIri] = (^.asInstanceOf[js.Dynamic].applyDynamic("prefixes")(defaultPrefixes.asInstanceOf[js.Any], factory.asInstanceOf[js.Any])).asInstanceOf[js.Function1[/* prefix */ String, PrefixedToIri]]
}
