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

import cats.effect.kernel.{Ref, Sync}
import cats.syntax.all.*
import cats.{FlatMap, MonadError}
import io.chrisdavenport.mules.{Cache,LocalSearch}
import org.http4s.Uri
import run.cosy.http.cache.DirTree.*
import run.cosy.http.cache.TreeDirCache.WebCache

object TreeDirCache:
   type Server = (Uri.Scheme, Uri.Authority)
   type WebCache[X] = Map[Server, DirTree[Option[X]]]

sealed trait TreeDirException extends Exception
case class ServerNotFound(uri: Uri) extends TreeDirException
case class IncompleteServiceInfo(uri: Uri) extends TreeDirException

/** this is a mules cache, but we add a method to search the path */
case class TreeDirCache[F[_], X](
    cacheRef: Ref[F, WebCache[X]]
)(using F: Sync[F])
    extends Cache[F, Uri, X]:

   /* todo: consider if that is really wise. It doe */
   override def delete(k: Uri): F[Unit] =
     if k.path.segments.isEmpty && !k.path.endsWithSlash
     then F.pure(())
     else
        for
           scheme <- F.fromOption(k.scheme, IncompleteServiceInfo(k))
           auth <- F.fromOption(k.authority, IncompleteServiceInfo(k))
           server = (scheme, auth)
           _ <- cacheRef.update { webCache =>
             webCache.get(server) match
              case Some(tree) => webCache.updated(server, tree.set(k.path.segments, None))
              case None => webCache
           }
        yield ()

     /** Deleting a server without a path, results in loosing all info in the path. todo: consider
       * if that is really wise. It doe
       */
   def deleteBelow(k: Uri): F[Unit] =
     for
        scheme <- F.fromOption(k.scheme, IncompleteServiceInfo(k))
        auth <- F.fromOption(k.authority, IncompleteServiceInfo(k))
        server = (scheme, auth)
        _ <- cacheRef.update { webCache =>
          if k.path.segments.isEmpty && !k.path.endsWithSlash
          then webCache - server
          else
             webCache.get(server).map(_.setDirAt(k.path.segments, DirTree.pure(None))) match
              case Some(tree) => webCache.updated(server, tree)
              case None => webCache
        }
     yield ()

   override def insert(k: Uri, v: X): F[Unit] =
     for
        scheme <- F.fromOption(k.scheme, IncompleteServiceInfo(k))
        auth <- F.fromOption(k.authority, IncompleteServiceInfo(k))
        server = (scheme, auth)
        _ <- cacheRef.update { webCache =>
           val tree = webCache.getOrElse(server, DirTree.pure(None))
           webCache.updated(server, tree.insertAt(k.path.segments, Some(v), None))
        }
     yield ()

   override def lookup(k: Uri): F[Option[X]] =
     for
        scheme <- F.fromOption(k.scheme, IncompleteServiceInfo(k))
        auth <- F.fromOption(k.authority, IncompleteServiceInfo(k))
        webCache <- cacheRef.get
        server = (scheme, auth)
        tree <- F.fromOption(webCache.get(server), ServerNotFound(k))
     yield
        val (path, v) = tree.find(k.path.segments)
        if path.isEmpty then v else None

   // /** find the closest node matching `select` going backwards from the closest node we have leading
   //   * to path. So if we want <people/henry/blog/2023/04/01/world-at-peace> but we have
   //   * <people/henry/blog/2023> and </people/henry/blog/> but only that content at the latter
   //   * resource matches, then we will get that.
   //   */
   // def findClosest(k: Uri)(predicate: Option[X] => Boolean): F[Option[X]] =
   //   for
   //      scheme <- F.fromOption(k.scheme, IncompleteServiceInfo(k))
   //      auth <- F.fromOption(k.authority, IncompleteServiceInfo(k))
   //      webCache <- cacheRef.get
   //      server = (scheme, auth)
   //      tree <- F.fromOption(webCache.get(server), ServerNotFound(k))
   //   yield tree.findClosest(k.path.segments)(predicate).flatten

