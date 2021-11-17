package n3js.node.anon

import n3js.node.nodeBooleans.`true`
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* Inlined node.node:fs.StatOptions & {  bigint :true} */
@js.native
trait StatOptionsbiginttrue extends StObject {
  
  var bigint: js.UndefOr[Boolean] with `true` = js.native
}
object StatOptionsbiginttrue {
  
  @scala.inline
  def apply(bigint: js.UndefOr[Boolean] with `true`): StatOptionsbiginttrue = {
    val __obj = js.Dynamic.literal(bigint = bigint.asInstanceOf[js.Any])
    __obj.asInstanceOf[StatOptionsbiginttrue]
  }
  
  @scala.inline
  implicit class StatOptionsbiginttrueMutableBuilder[Self <: StatOptionsbiginttrue] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setBigint(value: js.UndefOr[Boolean] with `true`): Self = StObject.set(x, "bigint", value.asInstanceOf[js.Any])
  }
}
