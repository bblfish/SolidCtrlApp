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
      EmberClientBuilder.default[IO].build.use { (client: Client[IO]) =>
        given web: Web[IO, JR] = new H4Web[IO, JR](client)
        val spider: LdesSpider[IO, JR] = new LdesSpider[IO, JR]
        println("streamUri="+stream)
        val streamUri: RDF.URI[JR] = ops.URI(stream)
        spider.crawl(streamUri)
      }
    val l: IO[List[RDF.Graph[JR]]] =
      fs2.Stream.eval(ioStr).flatten
        .reduce((g1, g2) => g1.union(g2))
        .compile[IO, IO, RDF.Graph[JR]]
        .toList
    l.unsafeRunSync().foreach(_.triples.foreach(println))
  end crawlContainer
  
//  def crawlNodes(): Unit =
//    import cats.syntax.all.toFlatMapOps
//    val lstOfGrIO: IO[List[RDF.Graph[JR]]] =
//      EmberClientBuilder.default[IO].build.use { (client: Client[IO]) =>
//        given web: Web[IO, JR] = new H4Web[IO, JR](client)
//        val spider: LdesSpider[IO, JR] = new LdesSpider[IO, JR]
//        val collectionURI =
//          URI("http://localhost:8080/ldes/miniCityFlows/")
//        val startNodeURI =
//          URI("http://localhost:8080/ldes/miniCityFlows/2021-09-05")
//        val startNodePNG = UriNGraph[JR](startNodeURI, collectionURI, Graph.empty)
//        for
//          ref <- Ref.of[IO, Set[RDF.URI[JR]]](Set())
//          lst <- spider
//            .crawlNodesForward(collectionURI, Seq(startNodePNG), ref)
//            .compile[IO, IO, RDF.Graph[JR]]
//            .toList
//        yield lst
//      }
//    val gr: Seq[RDF.Graph[JR]] = lstOfGrIO.unsafeRunSync()
//    val oneGr: RDF.Graph[JR] = gr.reduce((g1, g2) => g1.union(g2))
//    ???

end MiniCF
