package n3js.n3.mod

// import n3js.node.eventsMod.EventEmitter
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.ThisFunction1
import scala.scalajs.js.annotation.JSName

/** + This is generated from the typescript module for N3 JS library, but we
  * added a few methods and intend to change the behavior. Lexer objects should
  * therefore only be created with apply method in the companion object.
  *
  * Because we need to rewrite some functions, we add the protected functions
  * starting with an `_`.
  */
@JSImport("n3", "Lexer")
@js.native
class Lexer() extends StObject:
  def this(options: LexerOptions) = this()

  def tokenize(input: String): js.Array[Token] = js.native
  def tokenize(input: String, callback: TokenCallback): Unit = js.native
//   def tokenize(input: EventEmitter, callback: TokenCallback): Unit = js.native

  //
  // protected functions and vars we need access to in our code
  //
  var _line: Int = js.native
  var _input: js.UndefOr[String] = js.native
  var _callback: js.UndefOr[TokenCallback] = js.native
  def _tokenizeToEnd(callback: TokenCallback, inputFinished: Boolean): Unit = js.native

  //
  // functions we create 
  // 
  var setCallback: js.ThisFunction1[Lexer,TokenCallback, Unit] = js.native

  //this function is added during object creation
  def tokenizeChunk(input: String, inputFinished: Boolean): Unit = js.native

end Lexer

object Lexer:
	def apply(options: LexerOptions = LexerOptions()): Lexer =
		println("Lexer: Hello")
		val lex: Lexer = new Lexer(options)
		lex._input = ""
		//could one set these on the prototype?
		lex.asInstanceOf[js.Dynamic].updateDynamic("setCallback")(setCallback)
		lex.asInstanceOf[js.Dynamic].updateDynamic("tokenizeChunk")(tokenizeChunk)
		println("Lexer created")
		lex

	//must be called on initialisation
	val setCallback: js.ThisFunction1[Lexer, TokenCallback, Unit] =
		(thiz: Lexer, tokCbk: TokenCallback) => 
			println("Lexer.setCallback start")
			thiz._line = 1
			thiz._input = ""
			thiz._callback = tokCbk
			println("Lexer.setCallback end")
			()

	//this is the function f in `input.on('data', f)`			
	val tokenizeChunk: js.ThisFunction2[Lexer, String, Boolean, Unit] =
		(thiz: Lexer, chunk: String, end: Boolean) => 
			thiz._input =  thiz._input.orElse("").map(_ + chunk)
			thiz._callback.map(tcb => thiz._tokenizeToEnd(tcb, end))
			()
		

end Lexer