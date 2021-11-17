package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "BaseQuad")
@js.native
class BaseQuad protected ()
  extends StObject
     with n3js.rdfjsTypes.dataModelMod.BaseQuad {
  def this(subject: Term, predicate: Term, `object`: Term) = this()
  def this(subject: Term, predicate: Term, `object`: Term, graph: Term) = this()
  
  def equals(other: n3js.rdfjsTypes.dataModelMod.BaseQuad): Boolean = js.native
  
  def toJSON(): String = js.native
}
