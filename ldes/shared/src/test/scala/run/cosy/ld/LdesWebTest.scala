package run.cosy.ld
import cats.effect.IO
import org.w3.banana.RDF
import org.w3.banana.Ops
import run.cosy.ld.ldes.MiniLdesWWW
import munit.CatsEffectSuite

trait LdesWebTest[R <: RDF]()(using ops: Ops[R]) extends CatsEffectSuite:

  val miniWeb = new MiniLdesWWW[R]
  import miniWeb.foaf
  given www: Web[IO, R] = miniWeb
  
  test("") {
    
  }
