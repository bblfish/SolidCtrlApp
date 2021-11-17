package n3js.node

import n3js.std.Iterable
import n3js.std.IterableIterator
import n3js.std.Record
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object urlMod {
  
  /**
    * The `URLSearchParams` API provides read and write access to the query of a`URL`. The `URLSearchParams` class can also be used standalone with one of the
    * four following constructors.
    * The `URLSearchParams` class is also available on the global object.
    *
    * The WHATWG `URLSearchParams` interface and the `querystring` module have
    * similar purpose, but the purpose of the `querystring` module is more
    * general, as it allows the customization of delimiter characters (`&#x26;` and `=`).
    * On the other hand, this API is designed purely for URL query strings.
    *
    * ```js
    * const myURL = new URL('https://example.org/?abc=123');
    * console.log(myURL.searchParams.get('abc'));
    * // Prints 123
    *
    * myURL.searchParams.append('abc', 'xyz');
    * console.log(myURL.href);
    * // Prints https://example.org/?abc=123&#x26;abc=xyz
    *
    * myURL.searchParams.delete('abc');
    * myURL.searchParams.set('a', 'b');
    * console.log(myURL.href);
    * // Prints https://example.org/?a=b
    *
    * const newSearchParams = new URLSearchParams(myURL.searchParams);
    * // The above is equivalent to
    * // const newSearchParams = new URLSearchParams(myURL.search);
    *
    * newSearchParams.append('a', 'c');
    * console.log(myURL.href);
    * // Prints https://example.org/?a=b
    * console.log(newSearchParams.toString());
    * // Prints a=b&#x26;a=c
    *
    * // newSearchParams.toString() is implicitly called
    * myURL.search = newSearchParams;
    * console.log(myURL.href);
    * // Prints https://example.org/?a=b&#x26;a=c
    * newSearchParams.delete('a');
    * console.log(myURL.href);
    * // Prints https://example.org/?a=b&#x26;a=c
    * ```
    * @since v7.5.0, v6.13.0
    */
  @JSImport("url", "URLSearchParams")
  @js.native
  class URLSearchParams ()
    extends StObject
       with Iterable[js.Tuple2[String, String]] {
    def this(init: String) = this()
    def this(init: js.Array[js.Tuple2[String, String]]) = this()
    def this(init: js.Iterable[js.Tuple2[String, String]]) = this()
    def this(init: URLSearchParams) = this()
    def this(init: Record[String, String | js.Array[String]]) = this()
    
    /**
      * Append a new name-value pair to the query string.
      */
    def append(name: String, value: String): Unit = js.native
    
    /**
      * Remove all name-value pairs whose name is `name`.
      */
    def delete(name: String): Unit = js.native
    
    /**
      * Returns an ES6 `Iterator` over each of the name-value pairs in the query.
      * Each item of the iterator is a JavaScript `Array`. The first item of the `Array`is the `name`, the second item of the `Array` is the `value`.
      *
      * Alias for `urlSearchParams[@@iterator]()`.
      */
    def entries(): IterableIterator[js.Tuple2[String, String]] = js.native
    
    /**
      * Iterates over each name-value pair in the query and invokes the given function.
      *
      * ```js
      * const myURL = new URL('https://example.org/?a=b&#x26;c=d');
      * myURL.searchParams.forEach((value, name, searchParams) => {
      *   console.log(name, value, myURL.searchParams === searchParams);
      * });
      * // Prints:
      * //   a b true
      * //   c d true
      * ```
      * @param fn Invoked for each name-value pair in the query
      * @param thisArg To be used as `this` value for when `fn` is called
      */
    def forEach[TThis](
      callback: js.ThisFunction3[
          /* this */ TThis, 
          /* value */ String, 
          /* name */ String, 
          /* searchParams */ this.type, 
          Unit
        ]
    ): Unit = js.native
    def forEach[TThis](
      callback: js.ThisFunction3[
          /* this */ TThis, 
          /* value */ String, 
          /* name */ String, 
          /* searchParams */ this.type, 
          Unit
        ],
      thisArg: TThis
    ): Unit = js.native
    
    /**
      * Returns the value of the first name-value pair whose name is `name`. If there
      * are no such pairs, `null` is returned.
      * @return or `null` if there is no name-value pair with the given `name`.
      */
    def get(name: String): String | Null = js.native
    
    /**
      * Returns the values of all name-value pairs whose name is `name`. If there are
      * no such pairs, an empty array is returned.
      */
    def getAll(name: String): js.Array[String] = js.native
    
    /**
      * Returns `true` if there is at least one name-value pair whose name is `name`.
      */
    def has(name: String): Boolean = js.native
    
    @JSName(js.Symbol.iterator)
    var iterator_URLSearchParams: js.Function0[IterableIterator[js.Tuple2[String, String]]] = js.native
    
    /**
      * Returns an ES6 `Iterator` over the names of each name-value pair.
      *
      * ```js
      * const params = new URLSearchParams('foo=bar&#x26;foo=baz');
      * for (const name of params.keys()) {
      *   console.log(name);
      * }
      * // Prints:
      * //   foo
      * //   foo
      * ```
      */
    def keys(): IterableIterator[String] = js.native
    
    /**
      * Sets the value in the `URLSearchParams` object associated with `name` to`value`. If there are any pre-existing name-value pairs whose names are `name`,
      * set the first such pair's value to `value` and remove all others. If not,
      * append the name-value pair to the query string.
      *
      * ```js
      * const params = new URLSearchParams();
      * params.append('foo', 'bar');
      * params.append('foo', 'baz');
      * params.append('abc', 'def');
      * console.log(params.toString());
      * // Prints foo=bar&#x26;foo=baz&#x26;abc=def
      *
      * params.set('foo', 'def');
      * params.set('xyz', 'opq');
      * console.log(params.toString());
      * // Prints foo=def&#x26;abc=def&#x26;xyz=opq
      * ```
      */
    def set(name: String, value: String): Unit = js.native
    
    /**
      * Sort all existing name-value pairs in-place by their names. Sorting is done
      * with a [stable sorting algorithm](https://en.wikipedia.org/wiki/Sorting_algorithm#Stability), so relative order between name-value pairs
      * with the same name is preserved.
      *
      * This method can be used, in particular, to increase cache hits.
      *
      * ```js
      * const params = new URLSearchParams('query[]=abc&#x26;type=search&#x26;query[]=123');
      * params.sort();
      * console.log(params.toString());
      * // Prints query%5B%5D=abc&#x26;query%5B%5D=123&#x26;type=search
      * ```
      * @since v7.7.0, v6.13.0
      */
    def sort(): Unit = js.native
    
    /**
      * Returns an ES6 `Iterator` over the values of each name-value pair.
      */
    def values(): IterableIterator[String] = js.native
  }
  
  /**
    * Browser-compatible `URL` class, implemented by following the WHATWG URL
    * Standard. [Examples of parsed URLs](https://url.spec.whatwg.org/#example-url-parsing) may be found in the Standard itself.
    * The `URL` class is also available on the global object.
    *
    * In accordance with browser conventions, all properties of `URL` objects
    * are implemented as getters and setters on the class prototype, rather than as
    * data properties on the object itself. Thus, unlike `legacy urlObject` s,
    * using the `delete` keyword on any properties of `URL` objects (e.g. `delete myURL.protocol`, `delete myURL.pathname`, etc) has no effect but will still
    * return `true`.
    * @since v7.0.0, v6.13.0
    */
  @JSImport("url", "URL")
  @js.native
  class URL_ protected () extends StObject {
    def this(input: String) = this()
    def this(input: String, base: String) = this()
    def this(input: String, base: URL_) = this()
    
    /**
      * Gets and sets the fragment portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://example.org/foo#bar');
      * console.log(myURL.hash);
      * // Prints #bar
      *
      * myURL.hash = 'baz';
      * console.log(myURL.href);
      * // Prints https://example.org/foo#baz
      * ```
      *
      * Invalid URL characters included in the value assigned to the `hash` property
      * are `percent-encoded`. The selection of which characters to
      * percent-encode may vary somewhat from what the {@link parse} and {@link format} methods would produce.
      */
    var hash: String = js.native
    
    /**
      * Gets and sets the host portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://example.org:81/foo');
      * console.log(myURL.host);
      * // Prints example.org:81
      *
      * myURL.host = 'example.com:82';
      * console.log(myURL.href);
      * // Prints https://example.com:82/foo
      * ```
      *
      * Invalid host values assigned to the `host` property are ignored.
      */
    var host: String = js.native
    
    /**
      * Gets and sets the host name portion of the URL. The key difference between`url.host` and `url.hostname` is that `url.hostname` does _not_ include the
      * port.
      *
      * ```js
      * const myURL = new URL('https://example.org:81/foo');
      * console.log(myURL.hostname);
      * // Prints example.org
      *
      * // Setting the hostname does not change the port
      * myURL.hostname = 'example.com:82';
      * console.log(myURL.href);
      * // Prints https://example.com:81/foo
      *
      * // Use myURL.host to change the hostname and port
      * myURL.host = 'example.org:82';
      * console.log(myURL.href);
      * // Prints https://example.org:82/foo
      * ```
      *
      * Invalid host name values assigned to the `hostname` property are ignored.
      */
    var hostname: String = js.native
    
    /**
      * Gets and sets the serialized URL.
      *
      * ```js
      * const myURL = new URL('https://example.org/foo');
      * console.log(myURL.href);
      * // Prints https://example.org/foo
      *
      * myURL.href = 'https://example.com/bar';
      * console.log(myURL.href);
      * // Prints https://example.com/bar
      * ```
      *
      * Getting the value of the `href` property is equivalent to calling {@link toString}.
      *
      * Setting the value of this property to a new value is equivalent to creating a
      * new `URL` object using `new URL(value)`. Each of the `URL`object's properties will be modified.
      *
      * If the value assigned to the `href` property is not a valid URL, a `TypeError`will be thrown.
      */
    var href: String = js.native
    
    /**
      * Gets the read-only serialization of the URL's origin.
      *
      * ```js
      * const myURL = new URL('https://example.org/foo/bar?baz');
      * console.log(myURL.origin);
      * // Prints https://example.org
      * ```
      *
      * ```js
      * const idnURL = new URL('https://測試');
      * console.log(idnURL.origin);
      * // Prints https://xn--g6w251d
      *
      * console.log(idnURL.hostname);
      * // Prints xn--g6w251d
      * ```
      */
    val origin: String = js.native
    
    /**
      * Gets and sets the password portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://abc:xyz@example.com');
      * console.log(myURL.password);
      * // Prints xyz
      *
      * myURL.password = '123';
      * console.log(myURL.href);
      * // Prints https://abc:123@example.com
      * ```
      *
      * Invalid URL characters included in the value assigned to the `password` property
      * are `percent-encoded`. The selection of which characters to
      * percent-encode may vary somewhat from what the {@link parse} and {@link format} methods would produce.
      */
    var password: String = js.native
    
    /**
      * Gets and sets the path portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://example.org/abc/xyz?123');
      * console.log(myURL.pathname);
      * // Prints /abc/xyz
      *
      * myURL.pathname = '/abcdef';
      * console.log(myURL.href);
      * // Prints https://example.org/abcdef?123
      * ```
      *
      * Invalid URL characters included in the value assigned to the `pathname`property are `percent-encoded`. The selection of which characters
      * to percent-encode may vary somewhat from what the {@link parse} and {@link format} methods would produce.
      */
    var pathname: String = js.native
    
    /**
      * Gets and sets the port portion of the URL.
      *
      * The port value may be a number or a string containing a number in the range`0` to `65535` (inclusive). Setting the value to the default port of the`URL` objects given `protocol` will
      * result in the `port` value becoming
      * the empty string (`''`).
      *
      * The port value can be an empty string in which case the port depends on
      * the protocol/scheme:
      *
      * <omitted>
      *
      * Upon assigning a value to the port, the value will first be converted to a
      * string using `.toString()`.
      *
      * If that string is invalid but it begins with a number, the leading number is
      * assigned to `port`.
      * If the number lies outside the range denoted above, it is ignored.
      *
      * ```js
      * const myURL = new URL('https://example.org:8888');
      * console.log(myURL.port);
      * // Prints 8888
      *
      * // Default ports are automatically transformed to the empty string
      * // (HTTPS protocol's default port is 443)
      * myURL.port = '443';
      * console.log(myURL.port);
      * // Prints the empty string
      * console.log(myURL.href);
      * // Prints https://example.org/
      *
      * myURL.port = 1234;
      * console.log(myURL.port);
      * // Prints 1234
      * console.log(myURL.href);
      * // Prints https://example.org:1234/
      *
      * // Completely invalid port strings are ignored
      * myURL.port = 'abcd';
      * console.log(myURL.port);
      * // Prints 1234
      *
      * // Leading numbers are treated as a port number
      * myURL.port = '5678abcd';
      * console.log(myURL.port);
      * // Prints 5678
      *
      * // Non-integers are truncated
      * myURL.port = 1234.5678;
      * console.log(myURL.port);
      * // Prints 1234
      *
      * // Out-of-range numbers which are not represented in scientific notation
      * // will be ignored.
      * myURL.port = 1e10; // 10000000000, will be range-checked as described below
      * console.log(myURL.port);
      * // Prints 1234
      * ```
      *
      * Numbers which contain a decimal point,
      * such as floating-point numbers or numbers in scientific notation,
      * are not an exception to this rule.
      * Leading numbers up to the decimal point will be set as the URL's port,
      * assuming they are valid:
      *
      * ```js
      * myURL.port = 4.567e21;
      * console.log(myURL.port);
      * // Prints 4 (because it is the leading number in the string '4.567e21')
      * ```
      */
    var port: String = js.native
    
    /**
      * Gets and sets the protocol portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://example.org');
      * console.log(myURL.protocol);
      * // Prints https:
      *
      * myURL.protocol = 'ftp';
      * console.log(myURL.href);
      * // Prints ftp://example.org/
      * ```
      *
      * Invalid URL protocol values assigned to the `protocol` property are ignored.
      */
    var protocol: String = js.native
    
    /**
      * Gets and sets the serialized query portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://example.org/abc?123');
      * console.log(myURL.search);
      * // Prints ?123
      *
      * myURL.search = 'abc=xyz';
      * console.log(myURL.href);
      * // Prints https://example.org/abc?abc=xyz
      * ```
      *
      * Any invalid URL characters appearing in the value assigned the `search`property will be `percent-encoded`. The selection of which
      * characters to percent-encode may vary somewhat from what the {@link parse} and {@link format} methods would produce.
      */
    var search: String = js.native
    
    /**
      * Gets the `URLSearchParams` object representing the query parameters of the
      * URL. This property is read-only but the `URLSearchParams` object it provides
      * can be used to mutate the URL instance; to replace the entirety of query
      * parameters of the URL, use the {@link search} setter. See `URLSearchParams` documentation for details.
      *
      * Use care when using `.searchParams` to modify the `URL` because,
      * per the WHATWG specification, the `URLSearchParams` object uses
      * different rules to determine which characters to percent-encode. For
      * instance, the `URL` object will not percent encode the ASCII tilde (`~`)
      * character, while `URLSearchParams` will always encode it:
      *
      * ```js
      * const myUrl = new URL('https://example.org/abc?foo=~bar');
      *
      * console.log(myUrl.search);  // prints ?foo=~bar
      *
      * // Modify the URL via searchParams...
      * myUrl.searchParams.sort();
      *
      * console.log(myUrl.search);  // prints ?foo=%7Ebar
      * ```
      */
    val searchParams: URLSearchParams = js.native
    
    /**
      * The `toJSON()` method on the `URL` object returns the serialized URL. The
      * value returned is equivalent to that of {@link href} and {@link toString}.
      *
      * This method is automatically called when an `URL` object is serialized
      * with [`JSON.stringify()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON/stringify).
      *
      * ```js
      * const myURLs = [
      *   new URL('https://www.example.com'),
      *   new URL('https://test.example.org'),
      * ];
      * console.log(JSON.stringify(myURLs));
      * // Prints ["https://www.example.com/","https://test.example.org/"]
      * ```
      */
    def toJSON(): String = js.native
    
    /**
      * Gets and sets the username portion of the URL.
      *
      * ```js
      * const myURL = new URL('https://abc:xyz@example.com');
      * console.log(myURL.username);
      * // Prints abc
      *
      * myURL.username = '123';
      * console.log(myURL.href);
      * // Prints https://123:xyz@example.com/
      * ```
      *
      * Any invalid URL characters appearing in the value assigned the `username`property will be `percent-encoded`. The selection of which
      * characters to percent-encode may vary somewhat from what the {@link parse} and {@link format} methods would produce.
      */
    var username: String = js.native
  }
  object URL_
}
