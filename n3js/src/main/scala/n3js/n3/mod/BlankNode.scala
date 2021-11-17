package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "BlankNode")
@js.native
class BlankNode protected ()
  extends StObject
     with n3js.rdfjsTypes.dataModelMod.BlankNode
     with _QuadGraph
     with _QuadObject
     with _QuadSubject
     with _Term {
  def this(name: String) = this()
  
  val id: String = js.native
  
  def toJSON(): js.Object = js.native
}
/* static members */
object BlankNode {
  
  @JSImport("n3", "BlankNode")
  @js.native
  val ^ : js.Any = js.native
  
  @JSImport("n3", "BlankNode.nextId")
  @js.native
  def nextId: Double = js.native
  @scala.inline
  def nextId_=(x: Double): Unit = ^.asInstanceOf[js.Dynamic].updateDynamic("nextId")(x.asInstanceOf[js.Any])
  
  @scala.inline
  def subclass(`type`: js.Any): Unit = ^.asInstanceOf[js.Dynamic].applyDynamic("subclass")(`type`.asInstanceOf[js.Any]).asInstanceOf[Unit]
}
