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

package net.bblfish.wallet

import org.http4s.{ParseResult, Uri}
import org.http4s.headers.{Link, LinkValue}

class HttpTests extends munit.FunSuite {

  test(
    ("parse Link header. " +
      "ignore because of https://github.com/http4s/http4s/issues/7101").ignore
  ) {
    val ht1 = """<>; rel=https://www.w3.org/ns/auth/acl#accessControl"""
    val ht1res: ParseResult[Link] = Link.parse(ht1)
    assert(ht1res.isLeft, ht1res)
  }

  test("parse Link header working") {
    val h1 = """<http://localhost:8080/ldes/defaultCF/stream.acl>; rel=acl,""" +
      """ <http://localhost:8080/ldes/defaultCF/.acl>; rel="https://www.w3.org/ns/auth/acl#accessControl""""
    val pr1: ParseResult[Link] = Link.parse(h1)
    assert(pr1.isRight, pr1)

    val h2 = """<http://localhost:8080/ldes/defaultCF/stream.acl>; rel=acl,""" +
      """ <http://localhost:8080/ldes/defaultCF/.acl>; rel=https://www.w3.org/ns/auth/acl#accessControl"""
    val pr2: ParseResult[Link] = Link.parse(h2)
    assert(pr2.isLeft, pr2)

    val rfc8288Ex = """<http://example.org/>; rel="start http://example.net/relation/other""""
    val Right(Link(values)) = Link.parse(rfc8288Ex): @unchecked
    assertEquals(values.size, 1)
    assertEquals(
      values.head,
      LinkValue(
        Uri.unsafeFromString("http://example.org/"),
        rel = Some("start http://example.net/relation/other")
      )
    )
  }

}
