package n3js.node

import n3js.node.cryptoMod.BinaryLike
import n3js.node.workerThreadsMod._TransferListItem
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object nodeCryptoMod {
  
  /**
    * Encapsulates an X509 certificate and provides read-only access to
    * its information.
    *
    * ```js
    * const { X509Certificate } = await import('crypto');
    *
    * const x509 = new X509Certificate('{... pem encoded cert ...}');
    *
    * console.log(x509.subject);
    * ```
    * @since v15.6.0
    */
  @JSImport("node:crypto", "X509Certificate")
  @js.native
  class X509Certificate protected ()
    extends n3js.node.cryptoMod.X509Certificate
       with _TransferListItem {
    def this(buffer: BinaryLike) = this()
  }
}
