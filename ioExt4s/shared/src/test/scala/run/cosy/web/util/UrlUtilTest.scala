package run.cosy.web.util

import io.lemonlabs.uri as ll
import run.cosy.web.util.UrlUtil.*

class UrlUtilTest extends munit.FunSuite:
   def p(s: String) = ll.AbsoluteUrl.parseOption(s)
   val bblRoot = p("https://bblfish.net/")
   val bblCard = p("https://bblfish.net/people/henry/card#me")
   val henry = p("https://bblfish.net/people/henry/")
   val ppl = p("https://bblfish.net/people/")
   val bbldoc = p("https://bblfish.net")

   test("parent test") {
     assert(henry.isDefined)
     assertEquals(bblCard.flatMap(_.parent), henry)
     assert(henry.isDefined)
     assertEquals(henry.flatMap(_.parent), ppl)
     assertEquals(ppl.flatMap(_.parent), bblRoot)
     assertEquals(bblRoot.flatMap(_.parent), None)
   }
