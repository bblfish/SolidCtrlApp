package run.cosy.http.cache

import scala.collection.immutable.HashMap
import DirTree.*
import org.http4s.Uri

class DirTreeTest extends munit.FunSuite:
  import scala.language.implicitConversions
  implicit def s2p(s: String): Uri.Path.Segment = Uri.Path.Segment(s)
  implicit def ps2p(ps: Seq[String]): Uri.Path = Uri.Path(ps.map(s2p).toVector)
  def Pth(s: String*): Seq[Uri.Path.Segment] = s.map(s2p)
  def Mp(sdt: (String, DT)*): Map[Uri.Path.Segment, DT] =
    HashMap.from(sdt.map((s, dt) => Uri.Path.Segment(s) -> dt))
  type DT = DirTree[Int]
  val srcTstScHello = Pth("src", "test", "scala", "hello.txt")
  val fooBarBaz = Pth("foo", "bar", "baz")
  val srcTst = srcTstScHello.take(2)
  val src = srcTst.take(1)
  val srcTsSc = srcTstScHello.take(3)
  val one: DirTree[Int] = DirTree.pure[Int](1)
  val twoOne = DirTree(2, Mp("test" -> one))
  val threeTwoOne = DirTree(3, Mp("src" -> twoOne))

  import DirTree.*
  val testScHello = srcTstScHello.drop(1)

  test("findClosest") {
    assertEquals(one.find(Nil), (Nil, 1))
    assertEquals(one.find(fooBarBaz), (fooBarBaz, 1))
    assertEquals(one.find(srcTstScHello), (srcTstScHello, 1))

    assertEquals(twoOne.find(Nil), (Nil, 2))
    assertEquals(twoOne.find(fooBarBaz), (fooBarBaz, 2))
    assertEquals(twoOne.find(testScHello), (testScHello.drop(1), 1))

    assertEquals(threeTwoOne.find(Nil), (Nil, 3))
    assertEquals(threeTwoOne.find(fooBarBaz), (fooBarBaz, 3))
    assertEquals(threeTwoOne.find(srcTstScHello), (srcTstScHello.drop(2), 1))

  }

  test("toClosestPath") {
    assertEquals(
      threeTwoOne.unzipAlong(Nil),
      (Right(threeTwoOne), Nil)
    )
    assertEquals(
      threeTwoOne.unzipAlong(Pth("src")),
      (Right(twoOne), Seq(ZLink(threeTwoOne, "src")))
    )
    assertEquals(
      threeTwoOne.unzipAlong(Pth("src", "test")),
      (Right(one), Seq(ZLink(twoOne, "test"), ZLink(threeTwoOne, "src")))
    )
    // note that "scala" here is not in One. It is pointing to a place we may want something to be.
    assertEquals(
      threeTwoOne.unzipAlong(Pth("src", "test", "scala")),
      (Left(Nil), Seq(one -> "scala", twoOne -> "test", threeTwoOne -> "src"))
    )
    assertEquals(
      threeTwoOne.unzipAlong(Pth("src", "test", "scala", "hello.txt")),
      (Left(Pth("hello.txt")), List(one -> "scala", twoOne -> "test", threeTwoOne -> "src"))
    )
  }

  test("rezip") {
    assertEquals(one.rezip(Nil), one)
    assertEquals(one.rezip(Seq(DirTree.pure(2) -> "test")), twoOne)
    assertEquals(one.rezip(Seq(DirTree.pure(2) -> "test", DirTree.pure(3) -> "src")), threeTwoOne)
  }

  test("setAt") {
    assertEquals(one.insertAt(Nil, 2, 0), DirTree.pure(2))
    assertEquals(one.insertAt(Nil, -1, 0), DirTree.pure(-1))
    assertEquals(one.insertAt(Seq("two"), 2, 0), DirTree(1, Mp("two" -> DirTree.pure(2))))
    assertEquals(
      one.insertAt(Pth("two", "three"), 2, 0),
      DirTree(1, Mp("two" -> DirTree(0, Mp("three" -> DirTree.pure(2)))))
    )
    val tz1 = DirTree(3, Mp("zero" -> DirTree(0, Mp("one" -> DirTree.pure(1)))))
    assertEquals(DirTree.pure(3).insertAt(Seq("zero", "one"), 1, 0), tz1)
    val zom1t = DirTree(
      3,
      Mp(
        "zero" -> DirTree(
          0,
          Mp("one" -> DirTree(1, Mp("minus1" -> DirTree(-1, Mp("two" -> DirTree.pure(2))))))
        )
      )
    )
    assertEquals(tz1.insertAt(Seq("zero", "one", "minus1", "two"), 2, -1), zom1t)
    assertEquals(
      zom1t.insertAt(Pth("zero", "one"), 55555, -2),
      DirTree(
        3,
        Mp(
          "zero" -> DirTree(
            0,
            Mp("one" -> DirTree(55555, Mp("minus1" -> DirTree(-1, Mp("two" -> DirTree.pure(2))))))
          )
        )
      )
    )
  }
