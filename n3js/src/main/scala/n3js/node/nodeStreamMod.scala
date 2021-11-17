package n3js.node

import n3js.node.childProcessMod.StdioNull
import n3js.node.streamMod.ReadableOptions
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object nodeStreamMod {
  
  @JSImport("node:stream", "Stream")
  @js.native
  class Stream ()
    extends StObject
       with StdioNull {
    def this(opts: ReadableOptions) = this()
  }
}
