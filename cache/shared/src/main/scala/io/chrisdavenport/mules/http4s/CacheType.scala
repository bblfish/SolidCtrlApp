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

package io.chrisdavenport.mules.http4s

/** CacheTypes are in 2 flavors, private caches which are specifically for a single user, or public
  * caches which can be used for multiple users. Private caches can cache information set to
  * Cache-Control: private, whereas public caches are not allowed to cache that information
  */
sealed trait CacheType:
   /** Whether or not a Cache is Shared, public caches are shared, private caches are not
     */
   def isShared: Boolean = this match
    case CacheType.Private => false
    case CacheType.Public => true
object CacheType:
   case object Public extends CacheType
   case object Private extends CacheType
