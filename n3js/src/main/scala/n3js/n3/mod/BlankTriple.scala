package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait BlankTriple[Q /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */] extends StObject {
  
  var `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q['object'] */ js.Any = js.native
  
  var predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q['predicate'] */ js.Any = js.native
}
object BlankTriple {
  
  @scala.inline
  def apply[Q /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */](
    `object`: /* import warning: importer.ImportType#apply Failed type conversion: Q['object'] */ js.Any,
    predicate: /* import warning: importer.ImportType#apply Failed type conversion: Q['predicate'] */ js.Any
  ): BlankTriple[Q] = {
    val __obj = js.Dynamic.literal(predicate = predicate.asInstanceOf[js.Any])
    __obj.updateDynamic("object")(`object`.asInstanceOf[js.Any])
    __obj.asInstanceOf[BlankTriple[Q]]
  }
  
  @scala.inline
  implicit class BlankTripleMutableBuilder[Self <: BlankTriple[_], Q /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */] (val x: Self with BlankTriple[Q]) extends AnyVal {
    
    @scala.inline
    def setObject(value: /* import warning: importer.ImportType#apply Failed type conversion: Q['object'] */ js.Any): Self = StObject.set(x, "object", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setPredicate(
      value: /* import warning: importer.ImportType#apply Failed type conversion: Q['predicate'] */ js.Any
    ): Self = StObject.set(x, "predicate", value.asInstanceOf[js.Any])
  }
}
