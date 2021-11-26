package run.cosy.solid.app.http

import fs2.{Chunk, Pure, Stream}
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.*
import run.cosy.app.io.n3.N3Parser
import run.cosy.rdfjs.model.Quad

import scala.scalajs.js


class FetcherMunitTests extends munit.FunSuite {
	Fetcher.setupUI()

	test("HelloWorld") {
		assert(document.querySelectorAll("p").count(_.textContent == "Hello World") == 1)
	}

}
