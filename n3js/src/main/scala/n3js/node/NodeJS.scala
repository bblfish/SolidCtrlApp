package n3js.node

import n3js.node.anon.End
import n3js.node.bufferMod.global.Buffer
import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.eventsMod.global.NodeJS.EventEmitter
import org.scalablytyped.runtime.StringDictionary
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

//#endregion ArrayLike.at() end
/*----------------------------------------------*
*                                               *
*               GLOBAL INTERFACES               *
*                                               *
*-----------------------------------------------*/
object NodeJS {
  
  type ArrayBufferView = TypedArray | js.typedarray.DataView
  
  type Dict[T] = StringDictionary[js.UndefOr[T]]
  
  @js.native
  trait ReadableStream
    extends StObject
       with EventEmitter {
    
    def isPaused(): Boolean = js.native
    
    def pause(): this.type = js.native
    
    def pipe[T /* <: WritableStream */](destination: T): T = js.native
    def pipe[T /* <: WritableStream */](destination: T, options: End): T = js.native
    
    def read(): String | Buffer = js.native
    def read(size: Double): String | Buffer = js.native
    
    var readable: Boolean = js.native
    
    def resume(): this.type = js.native
    
    def setEncoding(encoding: BufferEncoding): this.type = js.native
    
    def unpipe(): this.type = js.native
    def unpipe(destination: WritableStream): this.type = js.native
    
    def unshift(chunk: String): Unit = js.native
    def unshift(chunk: String, encoding: BufferEncoding): Unit = js.native
    def unshift(chunk: js.typedarray.Uint8Array): Unit = js.native
    def unshift(chunk: js.typedarray.Uint8Array, encoding: BufferEncoding): Unit = js.native
    
    def wrap(oldStream: ReadableStream): this.type = js.native
  }
  
  type TypedArray = js.typedarray.Uint8Array | js.typedarray.Uint8ClampedArray | js.typedarray.Uint16Array | js.typedarray.Uint32Array | js.typedarray.Int8Array | js.typedarray.Int16Array | js.typedarray.Int32Array | BigUint64Array | BigInt64Array | js.typedarray.Float32Array | js.typedarray.Float64Array
  
  @js.native
  trait WritableStream
    extends StObject
       with EventEmitter {
    
    def end(): Unit = js.native
    def end(cb: js.Function0[Unit]): Unit = js.native
    def end(data: String): Unit = js.native
    def end(data: String, cb: js.Function0[Unit]): Unit = js.native
    def end(data: js.typedarray.Uint8Array): Unit = js.native
    def end(data: js.typedarray.Uint8Array, cb: js.Function0[Unit]): Unit = js.native
    def end(str: String, encoding: BufferEncoding): Unit = js.native
    def end(str: String, encoding: BufferEncoding, cb: js.Function0[Unit]): Unit = js.native
    def end(str: String, encoding: Unit, cb: js.Function0[Unit]): Unit = js.native
    
    var writable: Boolean = js.native
    
    def write(buffer: String): Boolean = js.native
    def write(buffer: String, cb: js.Function1[/* err */ js.UndefOr[js.Error | Null], Unit]): Boolean = js.native
    def write(buffer: js.typedarray.Uint8Array): Boolean = js.native
    def write(buffer: js.typedarray.Uint8Array, cb: js.Function1[/* err */ js.UndefOr[js.Error | Null], Unit]): Boolean = js.native
    def write(str: String, encoding: BufferEncoding): Boolean = js.native
    def write(
      str: String,
      encoding: BufferEncoding,
      cb: js.Function1[/* err */ js.UndefOr[js.Error | Null], Unit]
    ): Boolean = js.native
    def write(str: String, encoding: Unit, cb: js.Function1[/* err */ js.UndefOr[js.Error | Null], Unit]): Boolean = js.native
  }
}
