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

	val ntriples1: String =
		s"""<${bbl("#me")}> <${foaf("knows")}> <${w3c("timbl/card#i")}> .
			|<${bbl("#me")}> <${foaf("name")}> "Henry Story" .
			|""".stripMargin
	val ntriples2: String =
		s"""<${w3c("timbl/card#i")}> <${foaf("knows")}> <${bbl("#me")}> .
			 |<${bbl("timbl/card#i")}> <${foaf("name")}> "Tim Berners-Lee"@en .
			 |""".stripMargin

	test("Simple NTriples line Chunk Test") {
		val bigNTriples = ntriples1 + ntriples2

		//parse one chunk with 1 string
		assertEquals(bigNTriples.length, 291)
		val statementsStream: Stream[Pure, Quad] = N3Parser.parse(Stream(bigNTriples))
		val stats: Set[Quad] = statementsStream.toList.toSet
		//no bnodes, so we can do simple set equality
		assertEquals(stats.size, 4)

		//parse one chunk of two strings
		val chunkedStatements: Stream[Pure, Quad] = N3Parser.parse(Stream(ntriples1,ntriples2))
		val chkdSt = chunkedStatements.toList.toSet
		assertEquals(chkdSt.size,4)
		assertEquals(stats,chkdSt)

		//parse many chunks of many strings
		//split the doc into strings of max 11 chars.
		val chunkedStream: Stream[Pure, String] = Stream(bigNTriples.sliding(11,11).toList*)
		//partition those strings into chunks of three
		val streamOfChunkStr: Stream[Pure, String] = chunkedStream.chunkN(3).flatMap(cs => Stream.chunk(cs))
		assertEquals(streamOfChunkStr.toList.size, 27, "Total number of strings")
		assertEquals(streamOfChunkStr.chunks.toList.size, 9, "Total number of chunks")

		val buStream: Stream[Pure,Quad] = N3Parser.parse(streamOfChunkStr)
		val bs = buStream.toList.toSet
		assertEquals(bs.size,4)
		assertEquals(bs,stats)
	}
}
