package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "Literal")
@js.native
class Literal protected ()
  extends StObject
     with n3js.rdfjsTypes.dataModelMod.Literal
     with _QuadObject
     with _Term {
  def this(id: String) = this()
  
  val datatypeString: String = js.native
  
  val id: String = js.native
  
  def toJSON(): js.Object = js.native
}
/* static members */
object Literal {
  
  @JSImport("n3", "Literal")
  @js.native
  val ^ : js.Any = js.native
  
  @JSImport("n3", "Literal.langStringDatatype")
  @js.native
  val langStringDatatype: NamedNode[String] = js.native
  
  @scala.inline
  def subclass(`type`: js.Any): Unit = ^.asInstanceOf[js.Dynamic].applyDynamic("subclass")(`type`.asInstanceOf[js.Any]).asInstanceOf[Unit]
}
