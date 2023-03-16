package scripts

// This fetches data from a mini City Flows web server
import cats.effect.unsafe.IORuntime
import cats.effect.{Concurrent, IO, Ref, unsafe}
import fs2.Chunk
import org.http4s.EntityDecoder
import org.http4s.client.Client
import org.http4s.ember.client.*
import org.w3.banana.*
import org.w3.banana.RDF.Graph
import run.cosy.ld.http4s.{H4Web, RDFDecoders}
import run.cosy.ld.{PNGraph, UriNGraph, Web}
import run.cosy.ldes.LdesSpider

object MiniCF:

  type JR = org.w3.banana.jena.JenaRdf.type
  import org.w3.banana.*
  import org.w3.banana.io.{JsonLd, RDFXML, RelRDFReader, Turtle}
  import org.w3.banana.jena.JenaRdf.ops
  import ops.{*, given}
  import org.w3.banana.jena.io.JenaRDFReader.given
  given ior: unsafe.IORuntime = cats.effect.unsafe.IORuntime.global

  given rdfDecoders: RDFDecoders[IO, JR] = new RDFDecoders[IO, JR]

  @main
  def crawlContainer(stream: String = "http://localhost:8080/ldes/miniCityFlows/stream#"): Unit =
    val streamUri: RDF.URI[JR] = ops.URI(stream)
    
    val ioStr: IO[fs2.Stream[IO, Chunk[UriNGraph[JR]]]] =
      AnHttpSigClient.emberAuthClient.flatMap { (client: Client[IO]) =>
        given web: Web[IO, JR] = new H4Web[IO, JR](client)
        val spider: LdesSpider[IO, JR] = new LdesSpider[IO, JR]
        spider.crawl(streamUri)
      }
      
    val l: IO[List[RDF.Graph[JR]]] =
      fs2.Stream.eval(ioStr)
        .flatten
        .unchunks
        .map( uriNG => selectObs(uriNG, streamUri) )
        .unchunks
        .reduce((g1, g2) => g1.union(g2))
        .compile[IO, IO, RDF.Graph[JR]]
        .toList
    l.unsafeRunSync().foreach(_.triples.foreach(println))
  end crawlContainer

  import run.cosy.ldes.prefix as ldesPre
  val foaf = prefix.FOAF[JR]
  val tree = ldesPre.TREE[JR]
  val sosa = ldesPre.SOSA[JR]
  val wgs84 = ldesPre.WGS84[JR]
  val ldes = ldesPre.LDES[JR]

  /** select the observations in tree:Node of the ldes:Stream
   *
   * todo: it should be able to be given or fetch the shEx described in the
   * ldes:Stream to decide what to select
   * */
  def selectObs(page: UriNGraph[JR], streamUrl: RDF.URI[JR]): fs2.Chunk[RDF.Graph[JR]] =
    val collInPage = new UriNGraph[JR](streamUrl, page.name, page.graph)
    val obs: Seq[RDF.Graph[JR]] = collInPage
      .rel(tree.member)
      .map(png =>
        png.collect(
          rdf.typ,
          wgs84.location,
          sosa.hasSimpleResult,
          sosa.madeBySensor,
          sosa.observedProperty,
          sosa.resultTime
        )()
      )
    fs2.Chunk(obs *)
  //      .fold(Graph.empty)((g1, g2) => g1 union g2)
  

end MiniCF
