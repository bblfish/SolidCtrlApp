package run.cosy.solid.app.http

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.*
import fs2.{Chunk, Pure, Stream}
import run.cosy.app.io.n3.N3Parser
import run.cosy.rdfjs.model.Quad


class FetcherMunitTests extends munit.FunSuite {
	Fetcher.setupUI()

	test("HelloWorld") {
		assert(document.querySelectorAll("p").count(_.textContent == "Hello World") == 1)
	}

	def bbl(tag: String): String = "https://bblfish.net/"
	def w3c(tag: String): String = "https://w3.org/"
	def foaf(tag: String): String = "https://xmlns.com/foaf/0.1/"

	val chunk1: String =
		s"""<${bbl("#me")}> <${foaf("knows")}> <${w3c("timbl/card#i")}> .
			|<${bbl("#me")}> <${foaf("name")}> "Henry Story" .
			|""".stripMargin
	val chunk2: String =
		s"""<${w3c("timbl/card#i")}> <${foaf("knows")}> <${bbl("#me")}> .
			 |<${bbl("timbl/card#i")}> <${foaf("name")}> "Tim Berners-Lee"@en .
			 |""".stripMargin

	test("Simple NTriples line Chunk Test") {
		val bigChunk = chunk1 + chunk2
		println("bigChunk.length = "+bigChunk.length)
		val statementsStream: Stream[Pure, Quad] = N3Parser.parse(Stream(bigChunk))
		val stats: Set[Quad] = statementsStream.toList.toSet
		//no bnodes, so we can do simple set equality
		assertEquals(stats.size,4)

		val chunkedStatements: Stream[Pure, Quad] = N3Parser.parse(Stream(chunk1,chunk2))
		val chkdSt = chunkedStatements.toList.toSet
		assertEquals(chkdSt.size,4)
		assertEquals(stats,chkdSt)

		// let us chunk up the original string into chunks of 3 strings of max 10 chars.
		val chunkedStream: Stream[Pure, String] = Stream(bigChunk.sliding(10,10).toList*)
		val chunkOfChunkStr: Stream[Pure, String] = chunkedStream.chunkN(3).flatMap(cs => Stream.chunk(cs))
		assert(chunkOfChunkStr.toList.size,10)
		val buStream: Stream[Pure,Quad] = N3Parser.parse(chunkOfChunkStr)
		val bs = buStream.toList.toSet
		assertEquals(bs.size,4)
		assertEquals(bs,stats)
	}
}
