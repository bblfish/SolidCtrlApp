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

package run.cosy.ld

import cats.Id
import cats.effect.IO
import io.lemonlabs.uri.AbsoluteUrl
import org.w3.banana.{Ops, RDF}
import org.w3.banana.prefix
import org.w3.banana.diesel
import org.w3.banana.diesel.{*, given}

object MiniFoafWWW:
   val BblCard = "https://bblfish.net/people/henry/card"
   val Bbl = BblCard + "#me"
   val TimblCard = "https://www.w3.org/People/Berners-Lee/card"
   val Timbl = TimblCard + "#i"
   val EricPCard = "https://www.w3.org/People/Eric/ericP-foaf.rdf"
   val EricP = EricPCard + "#ericP"
   val CSarvenCard = "https://csarven.ca/"
   val CSarven = CSarvenCard + "#i"

class MiniFoafWWW[R <: RDF](using ops: Ops[R]) extends Web[IO, R]:
   import ops.{*, given}
   import MiniFoafWWW.*

   val foaf = prefix.FOAF[R]
   val fr = Lang("fr") // todo, put together a list of Lang constants
   val en = Lang("en")

   def getPNG(url: RDF.URI[R]): IO[UriNGraph[R]] =
      val doc = url.fragmentLess
      get(doc).map(g => new UriNGraph(url, doc, g))

   def get(url: RDF.URI[R]): IO[RDF.Graph[R]] =
      import scala.language.implicitConversions
      val res: RDF.rGraph[R] = url.value match
       case BblCard => (rURI("#me") -- foaf.name ->- "Henry Story".lang(en)
           -- foaf.knows ->- URI(EricP)
           -- foaf.knows ->- URI(CSarven)
           -- foaf.knows ->- URI(Timbl)
           -- foaf.knows ->- (BNode() -- foaf.name ->- "James Gosling")).graph
       case TimblCard => (rURI("#i") -- foaf.name ->- "Tim Berners-Lee".lang(en)
           -- foaf.knows ->- URI(Bbl)
           -- foaf.knows ->- URI(CSarven)
           -- foaf.knows ->- URI(EricP)
           -- foaf.knows ->- (BNode() -- foaf.name ->- "Vint Cerf")).graph
       case EricPCard => (rURI("#ericP") -- foaf.name ->- "Eric Prud'hommeaux".lang(fr)
           -- foaf.knows ->- URI(Bbl)
           -- foaf.knows ->- URI(CSarven)
           -- foaf.knows ->- URI(Timbl)).graph
       case CSarvenCard => (rURI("#i") -- foaf.name ->- "Sarven Capadisli".lang(en)
           -- foaf.knows ->- URI(Bbl)
           -- foaf.knows ->- URI(Timbl)
           -- foaf.knows ->- URI(EricP)).graph

       case _ => ops.rGraph.empty

      IO(res.resolveAgainst(AbsoluteUrl.parse(url.value)))
   end get
