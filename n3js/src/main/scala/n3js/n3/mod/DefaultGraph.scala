package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "DefaultGraph")
@js.native
class DefaultGraph ()
  extends StObject
     with n3js.rdfjsTypes.dataModelMod.DefaultGraph
     with _QuadGraph
     with _Term {
  
  val id: String = js.native
  
  def toJSON(): js.Object = js.native
}
/* static members */
object DefaultGraph {
  
  @JSImport("n3", "DefaultGraph")
  @js.native
  val ^ : js.Any = js.native
  
  @scala.inline
  def subclass(`type`: js.Any): Unit = ^.asInstanceOf[js.Dynamic].applyDynamic("subclass")(`type`.asInstanceOf[js.Any]).asInstanceOf[Unit]
}
