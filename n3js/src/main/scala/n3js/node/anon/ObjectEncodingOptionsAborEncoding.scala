package n3js.node.anon

import n3js.node.AbortSignal
import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.fsMod.Mode
import n3js.node.fsMod._WriteFileOptions
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* Inlined node.fs.ObjectEncodingOptions & node.node:events.Abortable & {  mode :node.fs.Mode | undefined,   flag :string | undefined} */
@js.native
trait ObjectEncodingOptionsAborEncoding
  extends StObject
     with _WriteFileOptions {
  
  var encoding: js.UndefOr[BufferEncoding | Null] = js.native
  
  var flag: js.UndefOr[String] = js.native
  
  var mode: js.UndefOr[Mode] = js.native
  
  /**
    * When provided the corresponding `AbortController` can be used to cancel an asynchronous action.
    */
  var signal: js.UndefOr[AbortSignal] = js.native
}
object ObjectEncodingOptionsAborEncoding {
  
  @scala.inline
  def apply(): ObjectEncodingOptionsAborEncoding = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[ObjectEncodingOptionsAborEncoding]
  }
  
  @scala.inline
  implicit class ObjectEncodingOptionsAborEncodingMutableBuilder[Self <: ObjectEncodingOptionsAborEncoding] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setEncodingNull: Self = StObject.set(x, "encoding", null)
    
    @scala.inline
    def setEncodingUndefined: Self = StObject.set(x, "encoding", js.undefined)
    
    @scala.inline
    def setFlag(value: String): Self = StObject.set(x, "flag", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFlagUndefined: Self = StObject.set(x, "flag", js.undefined)
    
    @scala.inline
    def setMode(value: Mode): Self = StObject.set(x, "mode", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setModeUndefined: Self = StObject.set(x, "mode", js.undefined)
    
    @scala.inline
    def setSignal(value: AbortSignal): Self = StObject.set(x, "signal", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setSignalUndefined: Self = StObject.set(x, "signal", js.undefined)
  }
}
