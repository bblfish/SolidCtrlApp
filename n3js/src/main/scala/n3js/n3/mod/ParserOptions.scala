package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait ParserOptions extends StObject {
  
  var baseIRI: js.UndefOr[String] = js.native
  
  var blankNodePrefix: js.UndefOr[String] = js.native
  
  var factory: js.UndefOr[run.cosy.rdfjs.model.DataFactory] = js.native
  
  // string type is here to maintain backwards compatibility - consider removing when
  // updating major version
  var format: js.UndefOr[String | MimeFormat] = js.native
}
object ParserOptions {
  
  @scala.inline
  def apply(): ParserOptions = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[ParserOptions]
  }
  
  @scala.inline
  implicit class ParserOptionsMutableBuilder[Self <: ParserOptions] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setBaseIRI(value: String): Self = StObject.set(x, "baseIRI", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setBaseIRIUndefined: Self = StObject.set(x, "baseIRI", js.undefined)
    
    @scala.inline
    def setBlankNodePrefix(value: String): Self = StObject.set(x, "blankNodePrefix", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setBlankNodePrefixUndefined: Self = StObject.set(x, "blankNodePrefix", js.undefined)
    
    @scala.inline
    def setFactory(value: run.cosy.rdfjs.model.DataFactory): Self = StObject.set(x, "factory", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFactoryUndefined: Self = StObject.set(x, "factory", js.undefined)
    
    @scala.inline
    def setFormat(value: String | MimeFormat): Self = StObject.set(x, "format", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setFormatUndefined: Self = StObject.set(x, "format", js.undefined)
  }
}
