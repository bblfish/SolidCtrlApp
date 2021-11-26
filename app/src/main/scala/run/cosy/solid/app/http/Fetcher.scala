package run.cosy.solid.app.http

import cats.effect.*
import cats.effect.unsafe.implicits.*
import fs2.{INothing, text}
import org.http4s.MediaType.text.turtle
import org.http4s.Method.GET
import org.http4s.dom.*
import org.http4s.implicits.*
import org.http4s.{QValue, Request, client}
import run.cosy.app.io.n3.N3Parser

import java.nio.charset.Charset
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Fetcher")
object Fetcher {

	import org.scalajs.dom
	import org.scalajs.dom.document

	val clnt: client.Client[IO] = FetchClientBuilder[IO].create
	// val rdfHeaders = org.http4s.Headers

	import org.http4s.client.dsl.io.given
	import org.http4s.headers.*

	val req: Request[IO] = GET(
		uri"https://bblfish.net/people/henry/card",
		Accept(turtle.withQValue(QValue.One))
	)

	lazy val answer: IO[String] = clnt.expect[String](req)

	@JSExport
	def addClickedMessage(): Unit =
		appendPar(document.body, "You clicked the button!")
		onClick()

	def appendPar(targetNode: dom.Node, text: String): Unit =
		val parNode = document.createElement("p")
		parNode.textContent = text
		targetNode.appendChild(parNode)

	def main(args: Array[String]): Unit =
		document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
			println(e)
		})

	def setupUI(): Unit = {
		val button = document.createElement("button")
		button.textContent = "Click me!"
		button.addEventListener("click", { (e: dom.MouseEvent) =>
			addClickedMessage()
		})
		document.body.appendChild(button)

		appendPar(document.body, "Hello World")
	}


	def onClick(): Unit =
		val utfStr: fs2.Stream[cats.effect.IO, String] = clnt.stream(req).flatMap(_.body)
			.through(text.utf8.decode)

		val ios: fs2.Stream[cats.effect.IO, INothing] = utfStr.through(N3Parser.parse)
			.foreach { triple =>
				IO(appendPar(document.body, triple.toString))
			}

		ios.compile.lastOrError.unsafeRunAsync {
			case Left(err) => appendPar(document.body, err.toString)
			case Right(answer) => appendPar(document.body, "good answer")
		}

}
