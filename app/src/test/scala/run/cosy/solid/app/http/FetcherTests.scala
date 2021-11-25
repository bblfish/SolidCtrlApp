//package run.cosy.solid.app.http
//
//import utest._
//
//import scala.scalajs.js
//import org.scalajs.dom
//import org.scalajs.dom.document
//import org.scalajs.dom.ext._
//
//
//class FetcherTests  extends TestSuite {
//
//	Fetcher.setupUI()
//
//	val tests = Tests {
//		test("HelloWorld") {
//			assert(document.querySelectorAll("p").count(_.textContent == "Hello World") == 1)
//		}
//
//		test("test1"){
//
//			throw new Exception("test1")
//		}
//		test("test2"){
//			assert(false)
//			1
//		}
//		test("test3"){
//			val a = List[Byte](1, 2)
//			a(10)
//		}
//	}
//}
