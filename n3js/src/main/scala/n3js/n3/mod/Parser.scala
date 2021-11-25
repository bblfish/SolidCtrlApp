package n3js.n3.mod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import run.cosy.rdfjs.model.{Quad,NamedNode}
import n3js.n3.mod.ParserOptions

@JSImport("n3", "Parser")
@js.native
class Parser() extends StObject:
	def this(options: ParserOptions) = this()
	import Parser.TokenFn

	def parse(input: String): js.Array[Quad] = js.native
//	 def parse(input: String, callback: ParseCallback[Q]): Unit = js.native
	def parse(
			input: String,
			callback: ParseCallback,
			prefixCallback: PrefixCallback
	): Unit = js.native
//	 def parse(input: String, callback: Null, prefixCallback: PrefixCallback): js.Array[Q] = js.native
//	 def parse(input: String, callback: Unit, prefixCallback: PrefixCallback): js.Array[Q] = js.native


	//
	// existing JS fields we make explicit
	//
	val _readInTopContext: TokenFn = js.native
	var _readCallback: TokenFn = js.native
	var _sparqlStyle: Boolean = js.native
	var _callback: ParseCallback = js.native
	var _prefixes: js.Object = js.native
	var _prefixCallback: PrefixCallback = js.native
	var _quantified: js.Object = js.native
	var _inversePredicate: Boolean = js.native
	var _lexer: Lexer = js.native
	//for testing
	var _emit: js.ThisFunction4[Parser, Quad.Subject, Quad.Predicate, Quad.Object, js.UndefOr[Quad.Graph], Unit] = js.native
	val _quad: js.Function4[Quad.Subject, Quad.Predicate, Quad.Object, js.UndefOr[Quad.Graph], Quad] = js.native

	//
	// functions we add
	//

	var setCallbacks : js.ThisFunction2[Parser, ParseCallback, PrefixCallback, Unit] = js.native
	
	// send the next string chunk to this parser. Need to create this method on construction.
	def parseChunk(chunk: String, end: Boolean): Unit = js.native
end Parser

object Parser:
	type TokenFn = js.ThisFunction1[Parser,Token,Any]

	def apply(options: ParserOptions): Parser = 
		val p = new Parser(options)
		p.asInstanceOf[js.Dynamic].updateDynamic("setCallbacks")(setCallbacks)
		p.asInstanceOf[js.Dynamic].updateDynamic("parseChunk")(parseChunk)
		p._lexer = Lexer()
		p

	//must be called on initialisation
	val setCallbacks : js.ThisFunction2[Parser, ParseCallback, PrefixCallback, Unit] = 
		(thiz: Parser, quadCallback: ParseCallback, prefixCallback: PrefixCallback) =>
			thiz._readCallback = thiz._readInTopContext
			thiz._sparqlStyle = false
			thiz._prefixes = new js.Object()
			// thiz._prefixes._ = ???
			thiz._prefixCallback = prefixCallback
			thiz._inversePredicate = false
			thiz._quantified = new js.Object()
			thiz._callback = quadCallback
			val callbackFn: TokenCallback = (err: js.UndefOr[js.Error], tok: js.UndefOr[n3js.n3.mod.Token]) =>
				if err != null && err.isDefined then
					thiz._callback(err,js.undefined,js.undefined)
//					thiz._callback = noopCallback
				else if tok != null && tok.isDefined then
					thiz._readCallback = thiz._readCallback(thiz, tok.get).asInstanceOf[TokenFn]
				else thiz._callback(new js.Error("don't have a callback to continue parsing"), js.undefined, js.undefined)	
			thiz._lexer.setCallback(thiz._lexer, callbackFn)
			()

	val noopCallback: ParseCallback = 
		(err: js.UndefOr[js.Error], quad: js.UndefOr[Quad], pre: js.UndefOr[n3js.n3.mod.Prefixes[NamedNode]]) => () 

	//this is the function f in `input.on('data', f)`			
	val parseChunk: js.ThisFunction2[Parser, String, Boolean, Unit] = 
		(thiz: Parser, chunk: String, end: Boolean) => 
			thiz._lexer.tokenizeChunk(chunk,end)
			()	
	
end Parser //obj

