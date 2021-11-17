package n3js.node.fsMod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait StatOptions extends StObject {
  
  var bigint: js.UndefOr[Boolean] = js.native
}
object StatOptions {
  
  @scala.inline
  def apply(): StatOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[StatOptions]
  }
  
  @scala.inline
  implicit class StatOptionsMutableBuilder[Self <: StatOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setBigint(value: Boolean): Self = StObject.set(x, "bigint", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setBigintUndefined: Self = StObject.set(x, "bigint", js.undefined)
  }
}
