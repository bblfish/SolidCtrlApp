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

package run.cosy.http.cache

import cats.MonadError
import cats.effect.{IO, Ref, SyncIO}
import munit.CatsEffectSuite
import org.http4s.Uri

class CacheTest extends CatsEffectSuite:
   import TreeDirCache.*
   import cats.MonadError.*
   import run.cosy.http.cache.DirTree

   def mkCache[X]: IO[TreeDirCache[IO, X]] =
     for wc <- Ref.of[IO, WebCache[X]](Map.empty)
     yield new TreeDirCache[IO, X](wc)

   val bbl = Uri.unsafeFromString("https://bblfish.net/people/henry/card#me")
   val bblPplDir = Uri.unsafeFromString("https://bblfish.net/people/")
   val bblRoot = Uri.unsafeFromString("https://bblfish.net/")
   val anais = Uri.unsafeFromString("https://bblfish.net/people/anais/card#i")

   val cacheIO: IO[TreeDirCache[IO, Int]] = mkCache[Int]

   test("test url paths") {
     assertEquals(bbl.path.segments.map(_.toString), Vector("people", "henry", "card"))
     assertEquals(bblPplDir.path.segments.map(_.toString), Vector("people"))
   }

   test("first test") {
     for
        cache <- cacheIO
        x <- cache.insert(bbl, 3)
        y <- cache.lookup(bbl)
     yield
        assertEquals(x, ())
        assertEquals(y, Some(3))
   }

   test("second test, does not capture the history of changes from the first.") {
     val iofail: IO[Unit] =
       for
          cache <- cacheIO
          y <- cache.lookup(bbl)
       yield ()
     interceptIO[ServerNotFound](iofail)
   }

   test("test searching for a parent") {
     val ioCache =
       for
          cache <- cacheIO
          _ <- cache.insert(bbl, 3)
          _ <- cache.insert(bblPplDir, 2)
          _ <- cache.insert(bblRoot, 0)
          x <- cache.lookup(bblPplDir)
          y <- cache.lookup(anais)
          _ <- cache.insert(anais, 12)
          y2 <- cache.lookup(anais)
          z <- cache.findClosest(bbl) { _ == Some(0) }
          w <- cache.findClosest(anais) { _ == Some(2) }
       yield
          assertEquals(x, Some(2))
          assertEquals(y, None)
          assertEquals(y2, Some(12))
          assertEquals(z, Some(0))
          assertEquals(w, Some(2))
          cache

     for
        cache <- ioCache
        a <- cache.lookup(anais)
        _ <- cache.delete(bblPplDir)
        x <- cache.lookup(bblPplDir)
        a2 <- cache.lookup(anais)
        _ <- cache.deleteBelow(bblPplDir)
        a3 <- cache.lookup(anais)
        y3 <- cache.lookup(bbl)
     yield
        assertEquals(a, Some(12))
        assertEquals(x, None)
        assertEquals(a2, Some(12))
        assertEquals(a3, None)
        assertEquals(y3, None)
   }
