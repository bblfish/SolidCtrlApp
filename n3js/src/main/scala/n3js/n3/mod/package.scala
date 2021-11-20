package n3js.n3

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}
import run.cosy.rdfjs.model.*

package object mod {
  
//   @scala.inline
//   def termFromId(
//     id: java.lang.String,
//     factory: run.cosy.rdfjs.model.DataFactory
// 	 ): n3js.n3.mod.Term = 
// 		 (n3js.n3.mod.^.asInstanceOf[js.Dynamic].applyDynamic("termFromId")(id.asInstanceOf[js.Any], 
// 		 factory.asInstanceOf[js.Any])).asInstanceOf[n3js.n3.mod.Term]
  
  @scala.inline
  def termToId(term: n3js.n3.mod.Term): java.lang.String = n3js.n3.mod.^.asInstanceOf[js.Dynamic].applyDynamic("termToId")(term.asInstanceOf[js.Any]).asInstanceOf[java.lang.String]
  
  type BaseFormatVariant = n3js.n3.mod.BaseFormat | n3js.std.Lowercase[n3js.n3.mod.BaseFormat]
  
  type ErrorCallback = js.Function2[/* err */ js.Error, /* result */ js.Any, scala.Unit]
  
  type MimeFormat = n3js.n3.mod.MimeSubtype | n3js.n3.n3Strings.DollarLeftcurlybracketMimeTypeRightcurlybracketSlashDollarLeftcurlybracketMimeSubtypeRightcurlybracket
  
  type MimeSubtype = n3js.n3.mod.BaseFormatVariant | n3js.n3.n3Strings.DollarLeftcurlybracketBaseFormatVariantRightcurlybracketDollarLeftcurlybracketStarRightcurlybracket
  
  type OTerm = run.cosy.rdfjs.model.Term[?] | java.lang.String | scala.Null
  
  type ParseCallback = js.Function3[
    /* error */ js.UndefOr[js.Error], 
    /* quad */ js.UndefOr[Quad], 
    /* prefixes */ js.UndefOr[n3js.n3.mod.Prefixes[NamedNode]], 
    scala.Unit
  ]
  
  type PrefixCallback = js.Function2[
    /* prefix */ java.lang.String, 
    /* prefixNode */ run.cosy.rdfjs.model.NamedNode, 
    scala.Unit
  ]
  
  type PrefixedToIri = js.Function1[/* suffix */ java.lang.String, run.cosy.rdfjs.model.NamedNode]
  
  type Prefixes[I] = org.scalablytyped.runtime.StringDictionary[I]
  
  type QuadCallback[Q /* <: n3js.n3.mod.BaseQuad */] = js.Function1[/* result */ Q, scala.Unit]
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.DefaultGraph
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadGraph = run.cosy.rdfjs.model.Quad.Graph | run.cosy.rdfjs.model.Variable
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.Literal
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadObject = run.cosy.rdfjs.model.Quad.Object | run.cosy.rdfjs.model.Variable
  
  type QuadPredicate[Q /* <: n3js.n3.mod.BaseQuad */] = js.Function1[/* result */ Q, scala.Boolean]
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Variable
  */
  type QuadSubject = run.cosy.rdfjs.model.Quad.Subject | run.cosy.rdfjs.model.Variable
  
  type Quad_Predicate = run.cosy.rdfjs.model.Quad.Predicate | run.cosy.rdfjs.model.Variable
  
  
  /* Rewritten from type alias, can be one of: 
    - n3js.n3.mod.NamedNode[java.lang.String]
    - n3js.n3.mod.BlankNode
    - n3js.n3.mod.Literal
    - n3js.n3.mod.Variable
    - n3js.n3.mod.DefaultGraph
  */
  type Term = run.cosy.rdfjs.model.Term[?] | run.cosy.rdfjs.model.Variable

  type TokenCallback = js.Function2[/* error */ js.UndefOr[js.Error], /* token */ js.UndefOr[n3js.n3.mod.Token], scala.Unit]
}
