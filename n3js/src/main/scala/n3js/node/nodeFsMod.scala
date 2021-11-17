package n3js.node

import n3js.node.streamMod.ReadableOptions
import n3js.node.streamMod.WritableOptions
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object nodeFsMod {
  
  @JSImport("node:fs", "ReadStream")
  @js.native
  class ReadStream () extends StObject {
    def this(opts: ReadableOptions) = this()
  }
  
  @JSImport("node:fs", "Stats")
  @js.native
  class Stats ()
    extends n3js.node.fsMod.Stats
  
  @JSImport("node:fs", "WriteStream")
  @js.native
  class WriteStream () extends StObject {
    def this(opts: WritableOptions) = this()
  }
}
