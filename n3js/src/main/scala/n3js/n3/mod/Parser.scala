package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("n3", "Parser")
@js.native
class Parser[Q /* <: BaseQuad */] () extends StObject {
  def this(options: ParserOptions) = this()
  
  def parse(input: String): js.Array[Q] = js.native
  def parse(input: String, callback: ParseCallback[Q]): Unit = js.native
  def parse(input: String, callback: ParseCallback[Q], prefixCallback: PrefixCallback): Unit = js.native
  def parse(input: String, callback: Null, prefixCallback: PrefixCallback): js.Array[Q] = js.native
  def parse(input: String, callback: Unit, prefixCallback: PrefixCallback): js.Array[Q] = js.native
}
