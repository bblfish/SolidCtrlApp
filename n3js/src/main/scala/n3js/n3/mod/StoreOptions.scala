package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait StoreOptions extends StObject {
  
  var factory: js.UndefOr[
    n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
  ] = js.native
}
object StoreOptions {
  
  @scala.inline
  def apply(): StoreOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[StoreOptions]
  }
  
  @scala.inline
  implicit class StoreOptionsMutableBuilder[Self <: StoreOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setFactory(
      value: n3js.rdfjsTypes.dataModelMod.DataFactory[n3js.rdfjsTypes.dataModelMod.Quad, n3js.rdfjsTypes.dataModelMod.Quad]
    ): Self = StObject.set(x, "factory", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFactoryUndefined: Self = StObject.set(x, "factory", js.undefined)
  }
}
