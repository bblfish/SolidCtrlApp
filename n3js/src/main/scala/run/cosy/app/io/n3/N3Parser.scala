package run.cosy.app.io.n3

import fs2.{Chunk, Stream}
import n3js.n3.mod.{ParseCallback, ParserOptions, PrefixCallback}
import run.cosy.rdfjs.model.{NamedNode, Quad}

import scala.scalajs.js

object N3Parser {
	def parse[F[_]](strs: Stream[F, String]): Stream[F, Quad] =
		val state = ParserState()
		state.parser.setCallbacks(state.parser, state.callback, noopPrefixCallback)
		strs.scanChunksOpt[ParserState, String, Quad](state) { state =>
			if state.terminated then None
			else
				Some { chunk =>
					chunk.foreach { str =>
						state.parser.parseChunk(str, false)
					}
					(state, state.takeQuads())
				}
		}

	val noopPrefixCallback: PrefixCallback =
		(pre: String, node: run.cosy.rdfjs.model.NamedNode) =>
			println(s"received prefix $pre: <$node>")
			()

	class ParserState private(val parser: n3js.n3.mod.Parser):
		var terminated: Boolean = false
		private var quads: List[Quad] = List()

		def takeQuads(): Chunk[Quad] =
			var chunk = Chunk(quads *)
			quads = List()
			chunk

		val callback: ParseCallback = (
			err: js.UndefOr[js.Error],
			quad: js.UndefOr[Quad],
			prefixes: js.UndefOr[n3js.n3.mod.Prefixes[NamedNode]]
		) =>
			if err != null && err.isDefined then terminated = true
			else if quad != null && quad.isDefined then
				quads = quad.get :: quads
			else if prefixes != null && prefixes.isDefined then terminated = true
		//else terminated = true ?

	end ParserState //class

	object ParserState:
		def apply(): ParserState =
			val df = run.cosy.rdfjs.model.DataFactory()
			val opt: ParserOptions = ParserOptions().setFactory(df)
			val ps = new ParserState(n3js.n3.mod.Parser(opt))
			ps

}
