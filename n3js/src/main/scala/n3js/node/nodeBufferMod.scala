package n3js.node

import n3js.node.bufferMod.BlobOptions
import n3js.node.cryptoMod.BinaryLike
import n3js.node.workerThreadsMod._TransferListItem
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object nodeBufferMod {
  
  @JSImport("node:buffer", "Blob")
  @js.native
  class Blob protected ()
    extends n3js.node.bufferMod.Blob
       with _TransferListItem {
    /**
      * Creates a new `Blob` object containing a concatenation of the given sources.
      *
      * {ArrayBuffer}, {TypedArray}, {DataView}, and {Buffer} sources are copied into
      * the 'Blob' and can therefore be safely modified after the 'Blob' is created.
      *
      * String sources are also copied into the `Blob`.
      */
    def this(sources: js.Array[BinaryLike | n3js.node.bufferMod.Blob]) = this()
    def this(sources: js.Array[BinaryLike | n3js.node.bufferMod.Blob], options: BlobOptions) = this()
  }
}
