package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object DataFactory {
  
  @JSImport("n3", "DataFactory")
  @js.native
  val ^ : js.Any = js.native
  
  @scala.inline
  def blankNode(): BlankNode = ^.asInstanceOf[js.Dynamic].applyDynamic("blankNode")().asInstanceOf[BlankNode]
  @scala.inline
  def blankNode(value: String): BlankNode = ^.asInstanceOf[js.Dynamic].applyDynamic("blankNode")(value.asInstanceOf[js.Any]).asInstanceOf[BlankNode]
  
  @scala.inline
  def defaultGraph(): DefaultGraph = ^.asInstanceOf[js.Dynamic].applyDynamic("defaultGraph")().asInstanceOf[DefaultGraph]
  
  @scala.inline
  def literal(value: String): Literal = ^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any]).asInstanceOf[Literal]
  @scala.inline
  def literal(value: String, languageOrDatatype: String): Literal = (^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any], languageOrDatatype.asInstanceOf[js.Any])).asInstanceOf[Literal]
  @scala.inline
  def literal(value: String, languageOrDatatype: n3js.rdfjsTypes.dataModelMod.NamedNode[String]): Literal = (^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any], languageOrDatatype.asInstanceOf[js.Any])).asInstanceOf[Literal]
  @scala.inline
  def literal(value: Double): Literal = ^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any]).asInstanceOf[Literal]
  @scala.inline
  def literal(value: Double, languageOrDatatype: String): Literal = (^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any], languageOrDatatype.asInstanceOf[js.Any])).asInstanceOf[Literal]
  @scala.inline
  def literal(value: Double, languageOrDatatype: n3js.rdfjsTypes.dataModelMod.NamedNode[String]): Literal = (^.asInstanceOf[js.Dynamic].applyDynamic("literal")(value.asInstanceOf[js.Any], languageOrDatatype.asInstanceOf[js.Any])).asInstanceOf[Literal]
  
  @scala.inline
  def namedNode[Iri /* <: String */](value: Iri): NamedNode[Iri] = ^.asInstanceOf[js.Dynamic].applyDynamic("namedNode")(value.asInstanceOf[js.Any]).asInstanceOf[NamedNode[Iri]]
  
  @scala.inline
  def quad(
    subject: n3js.rdfjsTypes.dataModelMod.QuadSubject,
    predicate: n3js.rdfjsTypes.dataModelMod.QuadPredicate,
    `object`: n3js.rdfjsTypes.dataModelMod.QuadObject
  ): Quad = (^.asInstanceOf[js.Dynamic].applyDynamic("quad")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any])).asInstanceOf[Quad]
  @scala.inline
  def quad(
    subject: n3js.rdfjsTypes.dataModelMod.QuadSubject,
    predicate: n3js.rdfjsTypes.dataModelMod.QuadPredicate,
    `object`: n3js.rdfjsTypes.dataModelMod.QuadObject,
    graph: n3js.rdfjsTypes.dataModelMod.QuadGraph
  ): Quad = (^.asInstanceOf[js.Dynamic].applyDynamic("quad")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any], graph.asInstanceOf[js.Any])).asInstanceOf[Quad]
  @scala.inline
  def quad[Q_In /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */, Q_Out /* <: BaseQuad */](
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['object'] */ js.Any
  ): Q_Out = (^.asInstanceOf[js.Dynamic].applyDynamic("quad")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any])).asInstanceOf[Q_Out]
  @scala.inline
  def quad[Q_In /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */, Q_Out /* <: BaseQuad */](
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['object'] */ js.Any,
    graph: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['graph'] */ js.Any
  ): Q_Out = (^.asInstanceOf[js.Dynamic].applyDynamic("quad")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any], graph.asInstanceOf[js.Any])).asInstanceOf[Q_Out]
  
  @scala.inline
  def triple(
    subject: n3js.rdfjsTypes.dataModelMod.QuadSubject,
    predicate: n3js.rdfjsTypes.dataModelMod.QuadPredicate,
    `object`: n3js.rdfjsTypes.dataModelMod.QuadObject
  ): Quad = (^.asInstanceOf[js.Dynamic].applyDynamic("triple")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any])).asInstanceOf[Quad]
  @scala.inline
  def triple[Q_In /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */, Q_Out /* <: BaseQuad */](
    subject: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['subject'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['predicate'] */ js.Any,
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q_In['object'] */ js.Any
  ): Q_Out = (^.asInstanceOf[js.Dynamic].applyDynamic("triple")(subject.asInstanceOf[js.Any], predicate.asInstanceOf[js.Any], `object`.asInstanceOf[js.Any])).asInstanceOf[Q_Out]
  
  @scala.inline
  def variable(value: String): Variable = ^.asInstanceOf[js.Dynamic].applyDynamic("variable")(value.asInstanceOf[js.Any]).asInstanceOf[Variable]
}
