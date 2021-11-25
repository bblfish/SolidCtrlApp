package run.cosy.solid.app.http

import cats.effect.*
import cats.effect.unsafe.implicits.*
import org.http4s.client
import org.http4s.dom.*
import org.http4s.{Request,QValue}
import org.http4s.implicits.*
import org.http4s.Method.GET
import org.http4s.MediaType.text.turtle
import cats.effect.IO
import scala.scalajs.js.annotation.{JSExport,JSExportTopLevel}
import fs2.text
import run.cosy.app.io.n3.N3Parser
import fs2.INothing
import java.nio.charset.Charset

@JSExportTopLevel("Fetcher")
object Fetcher {
	import org.scalajs.dom
	import org.scalajs.dom.document

	val clnt: client.Client[IO] = FetchClientBuilder[IO].create
	// val rdfHeaders = org.http4s.Headers
	import org.http4s.headers.*
	import org.http4s.client.dsl.io.{given}
	val req: Request[IO] = GET(
		uri"https://bblfish.net/people/henry/card",
		Accept(turtle.withQValue(QValue.One))
	)
	// // clnt.run(req)
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
		appendPar(document.body, "Hello World")

	def onClick() =	
		val utfStr: fs2.Stream[cats.effect.IO, String] = clnt.stream(req).flatMap(_.body)
			.through(text.utf8.decode)
	
		val ios: fs2.Stream[cats.effect.IO, INothing] = utfStr.through(N3Parser.parse)
			.foreach { triple => 
				IO(appendPar(document.body, triple.toString))
			}

		ios.compile.lastOrError.unsafeRunAsync{
			case Left(err)     => appendPar(document.body, err.toString)
			case Right(answer) => appendPar(document.body, "good answer")
		}

//	cl.expect
//

}
