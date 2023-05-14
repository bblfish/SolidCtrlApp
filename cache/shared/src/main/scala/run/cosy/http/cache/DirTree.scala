package run.cosy.http.cache

import cats.free.Cofree
import cats.Eval
import org.http4s.Uri.Path

import scala.annotation.tailrec
import scala.util.Right

object DirTree:
  //todo: we really need a Uri abstraction
  type Dir[X] = Map[org.http4s.Uri.Path.Segment, X]
  type DirTree[X] = Cofree[Dir[_], X]
  type Path = Seq[Path.Segment]

  /** A Path of DirTree[R,A]s is used to take apart a DirTree[R,A] structure, in the reverse
    * direction. Note the idea is that each link (name, dt) points from dt via name in the hashMap
    * to the next deeper node. The APath is in reverse direction so we have List( "newName" <- dir2
    * , "dir2in1" <- dir1 , "dir1" <- rootDir ) An empty List would just refer to the root
    * DirTree[R,A]
    */
  type ZPath[A] = Seq[ZLink[A]]

  /** A Zip Link
    * @param from
    *   the DirTree from which the link is pointing
    * @param linkName
    *   the name of the link. There may be nothing at the end of that.
    */
  case class ZLink[A](from: DirTree[A], linkName: Path.Segment)

  /** The context of an unzipped DirTree the first projection is either
    * -Right: the object at the end of the path
    * -Left: the remaining path. If it is Nil then the path is pointing into a position to which one
    * can add the second is the A Path, from the object to the root so that the object can be
    * reconstituted
    */
  type ZipContext[A] = (Either[Path, DirTree[A]], ZPath[A])

  def pure[X](x: X): DirTree[X] = Cofree(x, Eval.now(Map.empty))
  def apply[X](x: X, dirs: Dir[DirTree[X]]): DirTree[X] = Cofree(x, Eval.now(dirs))

  extension [X](thizDt: DirTree[X])
    def ->(name: Path.Segment): ZLink[X] = ZLink(thizDt, name)

    /** find the closest node X available when following Path
     * return the remaining path */
    @tailrec
    def find(at: Path): (Path, X) =
      at match
        case Seq() => (at, thizDt.head)
        case Seq(name, tail*) =>
          val dir: Dir[Cofree[Dir, X]] = thizDt.tail.value
          dir.get(name) match
            case None       => (at, thizDt.head)
            case Some(tree) => tree.find(tail)
    end find

    def unzipAlong(path: Path): ZipContext[X] =
      @tailrec
      def loop(dt: DirTree[X], path: Path, result: ZPath[X]): ZipContext[X] =
        path match
          case Seq() => (Right(dt), result)
          case Seq(name, rest*) =>
            if dt.tail.value.isEmpty then (Left(rest), ZLink(dt, name) +: result)
            else
              dt.tail.value.get(name) match
                case None => (Left(rest), ZLink(dt, name) +: result)
                case Some(dtchild) =>
                  loop(dtchild, rest, ZLink(dt, name) +: result)
      end loop

      loop(thizDt, path, Seq())
    end unzipAlong
   
     /** find the closest node matching `select` going backwards from where we got */
    def findClosest(path: Path)(select: X => Boolean): Option[X] =
      unzipAlong(path) match
        case (Right(dt), zpath) => dt.head +: zpath.map(_.from.head) find select
        case (Left(_), zpath)   => zpath.map(_.from.head) find select

    /** Rezip along zpath */
    def rezip(path: ZPath[X]): DirTree[X] =
      def loop(path: ZPath[X], dt: DirTree[X]): DirTree[X] =
        path match
          case Seq() => dt
          case Seq(ZLink(from, name), tail*) =>
            val newTail = from.tail.value + (name -> dt)
            loop(tail, from.copy(tail = Eval.now(newTail)))
      loop(path, thizDt)

    /** set value at path creating new directories with default values if needed */
    def insertAt(path: Path, value: X, default: X): DirTree[X] =
      thizDt.unzipAlong(path) match
        case (Left(path), zpath) =>
          pure(value).rezip(path.reverse.map(p => pure(default) -> p).appendedAll(zpath))
        case (Right(dt), zpath) => dt.copy(head = value).rezip(zpath)

    /** set dirTree at path creating new directories with default values if needed
      */
    def setDirAt(path: Path, dt: DirTree[X], default: X): DirTree[X] =
      thizDt.unzipAlong(path) match
        case (Left(path), zpath) => dt.rezip(path.map(p => pure(default) -> p).appendedAll(zpath))
        case (Right(dt), zpath)  => dt.rezip(zpath)
