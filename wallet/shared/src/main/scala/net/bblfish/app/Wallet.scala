/*
 * Copyright 2021 Typelevel
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

package net.bblfish.app

import io.lemonlabs.uri as ll
import org.http4s.{Request, Response}

import scala.util.Try

trait Wallet[F[_]] {

  /** if possible, sign the original request given the information provided by the 40x response.
    *
    * Note: For this to work, I think we need to assume that the URL in the Request is absolute. see
    * [[https://github.com/http4s/http4s/discussions/5930#discussioncomment-3777066 cats-uri discussion]]
    */
  def sign(failed: Response[F], lastReq: Request[F]): F[Request[F]]
}
