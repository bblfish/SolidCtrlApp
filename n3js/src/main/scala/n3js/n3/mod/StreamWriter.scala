package n3js.n3.mod

import n3js.node.eventsMod.EventEmitter
import n3js.node.streamMod.Transform
import n3js.rdfjsTypes.streamMod.Sink
import n3js.rdfjsTypes.streamMod.Stream
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@JSImport("n3", "StreamWriter")
@js.native
class StreamWriter[Q /* <: n3js.rdfjsTypes.dataModelMod.BaseQuad */] ()
  extends Transform
     with Sink[Stream[Q], EventEmitter] {
  def this(fd: js.Any) = this()
  def this(options: WriterOptions) = this()
  def this(fd: js.Any, options: WriterOptions) = this()
}
