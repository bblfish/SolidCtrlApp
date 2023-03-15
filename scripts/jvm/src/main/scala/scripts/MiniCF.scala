package scripts

// This fetches data from a mini City Flows web server
import cats.effect.unsafe.IORuntime
import cats.effect.{unsafe, Concurrent, IO, Ref}
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
    val ioStr: IO[fs2.Stream[IO, RDF.Graph[JR]]] =
      AnHttpSigClient.emberAuthClient.flatMap { (client: Client[IO]) =>
        given web: Web[IO, JR] = new H4Web[IO, JR](client)
        val spider: LdesSpider[IO, JR] = new LdesSpider[IO, JR]
        val streamUri: RDF.URI[JR] = ops.URI(stream)
        spider.crawl(streamUri)
      }
      
    val l: IO[List[RDF.Graph[JR]]] =
      fs2.Stream.eval(ioStr)
        .flatten
        .reduce((g1, g2) => g1.union(g2))
        .compile[IO, IO, RDF.Graph[JR]]
        .toList
    l.unsafeRunSync().foreach(_.triples.foreach(println))
  end crawlContainer
  

end MiniCF
