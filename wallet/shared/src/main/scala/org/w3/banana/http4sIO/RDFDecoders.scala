/*
 * Copyright 2021 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.w3.banana.http4sIO

import cats.{Applicative, FlatMap}
import cats.data.EitherT
import cats.effect.Concurrent
import org.http4s.EntityDecoder.collectBinary
import org.http4s.MediaType.{Compressible, NotBinary}
import org.http4s.{
  Charset,
  DecodeFailure,
  EntityDecoder,
  MalformedMessageBodyFailure,
  MediaRange,
  MediaType,
  QValue
}
import org.http4s.implicits.{qValue, given}
import org.w3.banana.io.{RDFXML, RelRDFReader, Turtle}
import org.w3.banana.io.JsonLd
import org.w3.banana.{Ops, RDF}

import scala.util.{Failure, Left, Right, Success, Try}
import org.http4s.client.Client
import org.http4s.headers.Accept
import org.w3.banana.RDF.rGraph

class RDFDecoders[F[_], Rdf <: RDF](using
    ops: Ops[Rdf],
    cc: Concurrent[F],
    turtleReader: RelRDFReader[Rdf, Try, Turtle],
    rdfXmlReader: RelRDFReader[Rdf, Try, RDFXML],
    jsonLDReader: RelRDFReader[Rdf, Try, JsonLd]
):

  private def decoderForRdfReader[T](mt: MediaRange, mts: MediaRange*)(
      reader: RelRDFReader[Rdf, Try, T],
      errmsg: String
  ): EntityDecoder[F, rGraph[Rdf]] =
    given defaultCharset: Charset = Charset.`UTF-8`
    EntityDecoder.decodeBy[F, rGraph[Rdf]](mt, mts: _*) { msg =>
      val txt: F[String] = EntityDecoder.decodeText[F](msg)
      EitherT[F, DecodeFailure, rGraph[Rdf]](
        cc.flatMap[String, Either[DecodeFailure, rGraph[Rdf]]](txt) { s =>
          reader.read(new java.io.StringReader(s)) match
            case Success(rg) => cc.pure(Right(rg))
            case Failure(err) =>
              cc.pure(Left(MalformedMessageBodyFailure(errmsg, Some(err))))
        }
      )
    }
  import MediaType.{application, text}
  import MediaType.application.`ld+json`

  // add to offiwcial types along with those described here:
  // https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/http/RDFMediaTypes.scala
  lazy val ntriples: MediaType =
    new MediaType(
      "application",
      "n-triples",
      Compressible,
      NotBinary,
      List("nt")
    )

  val turtleDecoder: EntityDecoder[F, rGraph[Rdf]] = decoderForRdfReader(text.turtle, ntriples)(
    turtleReader,
    "Rdf Turtle Reader failed"
  )
  val rdfxmlDecoder: EntityDecoder[F, rGraph[Rdf]] = decoderForRdfReader(application.`rdf+xml`)(
    rdfXmlReader,
    "Rdf Rdf/XML Reader failed"
  )
//  val ntriplesDecoder = decoderForRdfReader(application.`n-triples`)(
//    ntriplesReader, "NTriples  Reader failed"
//  )
  val jsonldDecoder: EntityDecoder[F, rGraph[Rdf]] = decoderForRdfReader(application.`ld+json`)(
    jsonLDReader,
    "Json-LD Reader failed"
  )

  given allrdf: EntityDecoder[F, rGraph[Rdf]] =
    turtleDecoder orElse rdfxmlDecoder orElse jsonldDecoder

  val allRdfAccept = Accept(
    text.turtle.withQValue(QValue.One),
    ntriples.withQValue(QValue.One),
    application.`ld+json`.withQValue(qValue"0.8"),
    application.`rdf+xml`.withQValue(qValue"0.8")
  )
end RDFDecoders
