package solidapp

import cats.effect.*
import cats.effect.unsafe.implicits.*
import fs2.{INothing, text}
import org.http4s.MediaType.text.turtle
import org.http4s.Method.GET
import org.http4s.dom.*
import org.http4s.implicits.*
import org.http4s.{ParseResult, QValue, Request, Uri, client}
import run.cosy.app.io.n3.N3Parser

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Example")
object Example:

	import org.scalajs.dom
	import dom.{document, html}

	val clnt: client.Client[IO] = FetchClientBuilder[IO].create
	// val rdfHeaders = org.http4s.Headers

	import org.http4s.client.dsl.io.given
	import org.http4s.headers.*

	def main(args: Array[String]): Unit =
		document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
			println(e)
		})

	def setupUI(): Unit =
		val button = document.createElement("button")
		button.textContent = "Click me!"
		button.addEventListener("click", { (e: dom.MouseEvent) =>
			addClickedMessage()
		})
		document.body.appendChild(button)
		appendPar(document.body, "Hello World")
	end setupUI

	@JSExport
	def addClickedMessage(): Unit =
		val url: ParseResult[Uri] = urlEntry()
		url.fold(
			fail => appendPar(document.body, "could not parse url " + fail),
			uri => {
				appendPar(document.body, "You clicked to fetch " + uri)
				onClick(uri)
			}
		)

	def urlEntry(): ParseResult[Uri] =
		val urlStr = input.value
		println("URL=" + urlStr)
		Uri.fromString(urlStr)

	def input: html.Input = document.getElementById("url").asInstanceOf[html.Input]

	def onClick(uri: Uri): Unit =
		val utfStr: fs2.Stream[cats.effect.IO, String] = clnt.stream(req(uri)).flatMap(_.body)
			.through(text.utf8.decode)

		val ios: fs2.Stream[cats.effect.IO, INothing] = utfStr.through(N3Parser.parse)
			.chunks.foreach { chunk =>
			IO(appendPar(document.body, s"chunk size ${chunk.size} starts with ${chunk.head}"))
		}

		ios.compile.lastOrError.unsafeRunAsync {
			case Left(err) => appendPar(document.body, err.toString)
			case Right(answer) => appendPar(document.body, "good answer")
		}
	end onClick

	def req(uri: Uri): Request[IO] = GET(uri, Accept(turtle.withQValue(QValue.One)))

	def appendPar(targetNode: dom.Node, text: String): Unit =
		val parNode = document.createElement("p")
		parNode.textContent = text
		targetNode.appendChild(parNode)

end Example