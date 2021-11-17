package n3js.n3

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

package object mod {
  
  @scala.inline
  def termFromId(
    id: java.lang.String,
    factory: n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
  ): n3js.n3.mod.Term = (n3js.n3.mod.^.asInstanceOf[js.Dynamic].applyDynamic("termFromId")(id.asInstanceOf[js.Any], factory.asInstanceOf[js.Any])).asInstanceOf[n3js.n3.mod.Term]
  
  @scala.inline
  def termToId(term: n3js.n3.mod.Term): java.lang.String = n3js.n3.mod.^.asInstanceOf[js.Dynamic].applyDynamic("termToId")(term.asInstanceOf[js.Any]).asInstanceOf[java.lang.String]
  
  type BaseFormatVariant = n3js.n3.mod.BaseFormat | n3js.std.Lowercase[n3js.n3.mod.BaseFormat]
  
  type ErrorCallback = js.Function2[/* err */ js.Error, /* result */ js.Any, scala.Unit]
  
  type MimeFormat = n3js.n3.mod.MimeSubtype | n3js.n3.n3Strings.DollarLeftcurlybracketMimeTypeRightcurlybracketSlashDollarLeftcurlybracketMimeSubtypeRightcurlybracket
  
  type MimeSubtype = n3js.n3.mod.BaseFormatVariant | n3js.n3.n3Strings.DollarLeftcurlybracketBaseFormatVariantRightcurlybracketDollarLeftcurlybracketStarRightcurlybracket
  
  type OTerm = n3js.rdfjsTypes.dataModelMod.Term | java.lang.String | scala.Null
  
  type ParseCallback[Q /* <: n3js.n3.mod.BaseQuad */] = js.Function3[
    /* error */ js.Error, 
    /* quad */ Q, 
    /* prefixes */ n3js.n3.mod.Prefixes[n3js.rdfjsTypes.dataModelMod.NamedNode[java.lang.String]], 
    scala.Unit
  ]
  
  type PrefixCallback = js.Function2[
    /* prefix */ java.lang.String, 
    /* prefixNode */ n3js.rdfjsTypes.dataModelMod.NamedNode[java.lang.String], 
    scala.Unit
  ]
  
  type PrefixedToIri = js.Function1[/* suffix */ java.lang.String, n3js.n3.mod.NamedNode[java.lang.String]]
  
  type Prefixes[I] = org.scalablytyped.runtime.StringDictionary[I]
  
  type QuadCallback[Q /* <: n3js.n3.mod.BaseQuad */] = js.Function1[/* result */ Q, scala.Unit]
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.DefaultGraph
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadGraph = n3js.n3.mod._QuadGraph | n3js.n3.mod.NamedNode[java.lang.String]
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.Literal
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadObject = n3js.n3.mod._QuadObject | n3js.n3.mod.NamedNode[java.lang.String]
  
  type QuadPredicate[Q /* <: n3js.n3.mod.BaseQuad */] = js.Function1[/* result */ Q, scala.Boolean]
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadSubject = n3js.n3.mod._QuadSubject | n3js.n3.mod.NamedNode[java.lang.String]
  
  type Quad_Predicate = n3js.n3.mod.NamedNode[java.lang.String] | n3js.n3.mod.Variable
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Literal
    - n3js.n3.mod.Variable
    - n3js.n3.mod.DefaultGraph
  */
  type Term = n3js.n3.mod._Term | n3js.n3.mod.NamedNode[java.lang.String]
  
  type TokenCallback = js.Function2[/* error */ js.Error, /* token */ n3js.n3.mod.Token, scala.Unit]
}
