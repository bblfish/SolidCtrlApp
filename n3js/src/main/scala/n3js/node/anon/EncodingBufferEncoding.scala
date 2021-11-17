package n3js.node.anon

import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.fsMod.OpenMode
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait EncodingBufferEncoding extends StObject {
  
  var encoding: BufferEncoding = js.native
  
  var flag: js.UndefOr[OpenMode] = js.native
}
object EncodingBufferEncoding {
  
  @scala.inline
  def apply(encoding: BufferEncoding): EncodingBufferEncoding = {
    val __obj = js.Dynamic.literal(encoding = encoding.asInstanceOf[js.Any])
    __obj.asInstanceOf[EncodingBufferEncoding]
  }
  
  @scala.inline
  implicit class EncodingBufferEncodingMutableBuilder[Self <: EncodingBufferEncoding] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFlag(value: OpenMode): Self = StObject.set(x, "flag", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFlagUndefined: Self = StObject.set(x, "flag", js.undefined)
  }
}
