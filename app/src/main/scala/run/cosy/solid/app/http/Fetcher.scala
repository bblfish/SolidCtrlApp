package solidapp

import cats.effect.*
import cats.effect.unsafe.implicits.*
import org.http4s.client
import org.http4s.dom.*
import org.http4s.{Request,QValue}
import org.scalajs.dom.*
import org.http4s.implicits.*
import org.http4s.Method.GET
import org.http4s.MediaType.text.turtle
import cats.effect.IO

object Fetcher {
	val clnt: client.Client[IO] = FetchClientBuilder[IO].create
	val rdfHeaders =org.http4s.Headers
	import org.http4s.headers.*
	import org.http4s.client.dsl.io.{given}
	val req: Request[IO] = GET(
		uri"https://bblfish.net/people/henry/card",
		Accept(turtle.withQValue(QValue.One))
	)
	val answer: IO[String] = clnt.expect[String](req)

	def main(args: Array[String]): Unit =
		answer.unsafeRunAsync{
			case Left(err)     => println(err)
			case Right(answer) => println(answer)
		}

//	cl.expect
//

}
