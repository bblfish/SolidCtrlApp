package solidapp

import outwatch._
import outwatch.dsl.{given,*}
import cats.effect.{IO, SyncIO}

import colibri.Subject

object Main {
  def main(args: Array[String]): Unit = {

    val counter = SyncIO {
      val number = Subject.behavior(0)
      div(
        button("+", onClick(number.map(_ + 1)) --> number),
        number,
      )
    }

    val app = div(
      h1("Hello World!"),
      counter,
    )

    OutWatch.renderInto[IO]("#app", app).unsafeRunSync()
  }
}
