package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait WriterOptions extends StObject {
  
  var end: js.UndefOr[Boolean] = js.native
  
  // string type is here to maintain backwards compatibility - consider removing when
  // updating major version
  var format: js.UndefOr[String | MimeFormat] = js.native
  
  var prefixes: js.UndefOr[Prefixes[n3js.rdfjsTypes.dataModelMod.NamedNode[String] | String]] = js.native
}
object WriterOptions {
  
  @scala.inline
  def apply(): WriterOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[WriterOptions]
  }
  
  @scala.inline
  implicit class WriterOptionsMutableBuilder[Self <: WriterOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setEnd(value: Boolean): Self = StObject.set(x, "end", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setEndUndefined: Self = StObject.set(x, "end", js.undefined)
    
    @scala.inline
    def setFormat(value: String | MimeFormat): Self = StObject.set(x, "format", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFormatUndefined: Self = StObject.set(x, "format", js.undefined)
    
    @scala.inline
    def setPrefixes(value: Prefixes[n3js.rdfjsTypes.dataModelMod.NamedNode[String] | String]): Self = StObject.set(x, "prefixes", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setPrefixesUndefined: Self = StObject.set(x, "prefixes", js.undefined)
  }
}
