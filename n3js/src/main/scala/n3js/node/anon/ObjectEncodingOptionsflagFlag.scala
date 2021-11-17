package n3js.node.anon

import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.fsMod.OpenMode
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* Inlined node.node:fs.ObjectEncodingOptions & {  flag :node.node:fs.OpenMode | undefined} */
@js.native
trait ObjectEncodingOptionsflagFlag extends StObject {
  
  var encoding: js.UndefOr[BufferEncoding | Null] = js.native
  
  var flag: js.UndefOr[OpenMode] = js.native
}
object ObjectEncodingOptionsflagFlag {
  
  @scala.inline
  def apply(): ObjectEncodingOptionsflagFlag = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[ObjectEncodingOptionsflagFlag]
  }
  
  @scala.inline
  implicit class ObjectEncodingOptionsflagFlagMutableBuilder[Self <: ObjectEncodingOptionsflagFlag] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setEncodingNull: Self = StObject.set(x, "encoding", null)
    
    @scala.inline
    def setEncodingUndefined: Self = StObject.set(x, "encoding", js.undefined)
    
    @scala.inline
    def setFlag(value: OpenMode): Self = StObject.set(x, "flag", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFlagUndefined: Self = StObject.set(x, "flag", js.undefined)
  }
}
