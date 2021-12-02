package run.cosy.solid.app.http

import cats.effect.*
import cats.effect.unsafe.implicits.*
import fs2.{INothing, text}
import org.http4s.MediaType.text.turtle
import org.http4s.Method.GET
import org.http4s.dom.*
import org.http4s.implicits.*
import org.http4s.{ParseResult, QValue, Request, Uri, client}
import run.cosy.app.io.n3.N3Parser


import java.nio.charset.Charset
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Fetcher")
object Fetcher {

}
