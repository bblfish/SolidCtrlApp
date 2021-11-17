package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait LexerOptions extends StObject {
  
  var comments: js.UndefOr[Boolean] = js.native
  
  var lineMode: js.UndefOr[Boolean] = js.native
  
  var n3: js.UndefOr[Boolean] = js.native
}
object LexerOptions {
  
  @scala.inline
  def apply(): LexerOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[LexerOptions]
  }
  
  @scala.inline
  implicit class LexerOptionsMutableBuilder[Self <: LexerOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setComments(value: Boolean): Self = StObject.set(x, "comments", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setCommentsUndefined: Self = StObject.set(x, "comments", js.undefined)
    
    @scala.inline
    def setLineMode(value: Boolean): Self = StObject.set(x, "lineMode", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setLineModeUndefined: Self = StObject.set(x, "lineMode", js.undefined)
    
    @scala.inline
    def setN3(value: Boolean): Self = StObject.set(x, "n3", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setN3Undefined: Self = StObject.set(x, "n3", js.undefined)
  }
}
