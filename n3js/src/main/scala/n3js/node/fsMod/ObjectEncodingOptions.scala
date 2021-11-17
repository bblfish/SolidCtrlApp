package n3js.node.fsMod

import n3js.node.bufferMod.global.BufferEncoding
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait ObjectEncodingOptions
  extends StObject
     with _EncodingOption {
  
  var encoding: js.UndefOr[BufferEncoding | Null] = js.native
}
object ObjectEncodingOptions {
  
  @scala.inline
  def apply(): ObjectEncodingOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[ObjectEncodingOptions]
  }
  
  @scala.inline
  implicit class ObjectEncodingOptionsMutableBuilder[Self <: ObjectEncodingOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setEncodingNull: Self = StObject.set(x, "encoding", null)
    
    @scala.inline
    def setEncodingUndefined: Self = StObject.set(x, "encoding", js.undefined)
  }
}
