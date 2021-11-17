package n3js.n3.mod

import n3js.node.eventsMod.EventEmitter
import n3js.node.streamMod.Transform
import n3js.rdfjsTypes.streamMod.Sink
import n3js.rdfjsTypes.streamMod.Stream
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* import warning: transforms.RemoveMultipleInheritance#findNewParents newComments Dropped parents 
- n3js.node.eventsMod.EventEmitter because Inheritance from two classes. Inlined 
- n3js.rdfjsTypes.streamMod.Stream because Inheritance from two classes. Inlined read */ @JSImport("n3", "StreamParser")
@js.native
class StreamParser[Q /* <: BaseQuad */] ()
  extends Transform
     with Sink[EventEmitter, Stream[Q]]
     with n3js.node.eventsMod.global.NodeJS.EventEmitter {
  def this(options: ParserOptions) = this()
  
  /**
    * This method pulls a quad out of the internal buffer and returns it.
    * If there is no quad available, then it will return null.
    *
    * @return A quad from the internal buffer, or null if none is available.
    */
  def read(): Q | Null = js.native
}
