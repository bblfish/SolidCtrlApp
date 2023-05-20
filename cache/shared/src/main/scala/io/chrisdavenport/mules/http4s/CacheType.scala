/** Copyright 2019 Christopher Davenport
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
  * and associated documentation files (the "Software"), to deal in the Software without
  * restriction, including without limitation the rights to use, copy, modify, merge, publish,
  * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
  * Software is furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all copies or
  * substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
  * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
  * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
