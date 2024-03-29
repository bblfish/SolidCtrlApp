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

package run.cosy.web.util

import com.comcast.ip4s
import io.lemonlabs.uri as ll
import org.http4s.Uri as h4Uri
import org.w3.banana.{Ops, RDF}

import scala.util.Try

object UrlUtil:

   extension (h4uri: org.http4s.Uri) def toLL: ll.Url = http4sUrlToLLUrl(h4uri)

   extension (llUri: ll.Url) def toh4: org.http4s.Uri = llUrltoHttp4s(llUri)

   extension (llUrl: ll.AbsoluteUrl)
     /** the parent url is the container containing this url most directly. Always ends in slash.
       * The root has itself as container
       */
     def parent: Option[ll.AbsoluteUrl] =
        given ll.config.UriConfig = ll.config.UriConfig.default
        val parentPathOpt = llUrl.path match
         case ap: ll.AbsolutePath => ap.parent
         case _ => None
        parentPathOpt.map(x =>
          llUrl.copy(
            path = x,
            query = ll.QueryString.empty,
            fragment = None
          )
        )

   extension (absPath: ll.AbsolutePath)
     def parent: Option[ll.AbsolutePath] =
        val dropedSlash =
          if absPath.parts.last == ""
          then absPath.parts.dropRight(1)
          else absPath.parts
        if dropedSlash.isEmpty
        then None
        else Some(new ll.AbsolutePath(dropedSlash.updated(dropedSlash.length - 1, "")))

   extension [R <: RDF](uri: RDF.URI[R])(using ops: Ops[R])
     def toLL: Try[ll.Uri] =
        import ops.{*, given}
        ll.Uri.parseTry(uri.value)

   // ignoring username:password urls
   def http4sUrlToLLUrl(u: org.http4s.Uri): ll.Url =
      import u.{
        host as h4host,
        path as h4path,
        query as h4query,
        port as h4port,
        scheme as h4scheme
      }
      ll.Url(
        h4scheme.map(_.value).getOrElse(null),
        null,
        null,
        h4host.map(_.value).getOrElse(null),
        h4port.getOrElse(-1),
        h4path.toString,
        ll.QueryString.parse(h4query.toString),
        null
      )
   end http4sUrlToLLUrl

   def llUrltoHttp4s(u: ll.Url): org.http4s.Uri =
      val h4schm: Option[h4Uri.Scheme] = u.schemeOption.flatMap { sch =>
        h4Uri.Scheme.fromString(sch).toOption
      }
      val h4Auth: Option[h4Uri.Authority] = u.authorityOption.flatMap { llAuth =>
         val ui: Option[h4Uri.UserInfo] = llAuth.userInfo
           .map(ui => h4Uri.UserInfo(ui.user, ui.password))
         val hostOpt: Option[h4Uri.Host] = ip4s.Host.fromString(llAuth.host.value)
           .map(h4Uri.Host.fromIp4sHost)
         hostOpt.map(host => h4Uri.Authority(ui, host, llAuth.port))
      }
      val h4path: h4Uri.Path = h4Uri.Path(u.path.parts.map(str => h4Uri.Path.Segment(str)))
      val h4q: org.http4s.Query = org.http4s.Query(u.query.params*)
      org.http4s.Uri(h4schm, h4Auth, h4path, h4q, u.fragment)
   end llUrltoHttp4s
