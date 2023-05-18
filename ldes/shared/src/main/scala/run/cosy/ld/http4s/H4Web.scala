/*
 * Copyright 2021 bblfish.net
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

package run.cosy.ld.http4s

import cats.effect.Concurrent
import io.lemonlabs.uri.Url
import org.http4s as h4s
import org.http4s.client.Client
import org.w3.banana.RDF
import org.w3.banana.RDF.URI
import run.cosy.ld.http4s.RDFDecoders
import run.cosy.ld.{UriNGraph, Web}
import run.cosy.web.util.UrlUtil.http4sUrlToLLUrl

/** Web implementation in http4s todo: we need a client that can find out about redirects, so that
  * we can correctly name the resource todo: we also need a web cache that can be queried and
  * updated (or perhaps that would use this)
  */
class H4Web[F[_]: Concurrent, R <: RDF](
    client: Client[F]
)(using
    val rdfDecoders: RDFDecoders[F, R]
) extends Web[F, R]:
   import cats.syntax.all.*
   import rdfDecoders.ops
   import rdfDecoders.allrdf
   import ops.{*, given}

   override def get(url: RDF.URI[R]): F[RDF.Graph[R]] =
     for
        u <- Concurrent[F].fromEither(h4s.Uri.fromString(url.value))
        doc = u.withoutFragment
        rG <- client.fetchAs[RDF.rGraph[R]](
          h4s.Request(uri = doc)
        )
     yield rG.resolveAgainst(http4sUrlToLLUrl(doc).toAbsoluteUrl)

   // todo: this should really use client.fetchPNG
   override def getPNG(url: RDF.URI[R]): F[UriNGraph[R]] =
      val doc = url.fragmentLess
      get(doc).map(g => UriNGraph(url, doc, g))
