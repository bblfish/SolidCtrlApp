package n3js.n3.mod

import n3js.node.eventsMod.EventEmitter
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("n3", "Lexer")
@js.native
class Lexer () extends StObject {
  def this(options: LexerOptions) = this()
  
  def tokenize(input: String): js.Array[Token] = js.native
  def tokenize(input: String, callback: TokenCallback): Unit = js.native
  def tokenize(input: EventEmitter, callback: TokenCallback): Unit = js.native
}
