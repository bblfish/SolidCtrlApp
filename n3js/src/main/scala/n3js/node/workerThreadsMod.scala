package n3js.node

import n3js.node.nodeStrings.close
import n3js.node.nodeStrings.message
import n3js.node.nodeStrings.messageerror
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object workerThreadsMod {
  
  /**
    * Instances of the `worker.MessagePort` class represent one end of an
    * asynchronous, two-way communications channel. It can be used to transfer
    * structured data, memory regions and other `MessagePort`s between different `Worker` s.
    *
    * This implementation matches [browser `MessagePort`](https://developer.mozilla.org/en-US/docs/Web/API/MessagePort) s.
    * @since v10.5.0
    */
  @JSImport("worker_threads", "MessagePort")
  @js.native
  class MessagePort ()
    extends StObject
       with _TransferListItem {
    
    def addListener(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def addListener(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("addListener")
    def addListener_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("addListener")
    def addListener_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("addListener")
    def addListener_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    /**
      * Disables further sending of messages on either side of the connection.
      * This method can be called when no further communication will happen over this`MessagePort`.
      *
      * The `'close' event` is emitted on both `MessagePort` instances that
      * are part of the channel.
      * @since v10.5.0
      */
    def close(): Unit = js.native
    
    def emit(event: String, args: js.Any*): Boolean = js.native
    def emit(event: js.Symbol, args: js.Any*): Boolean = js.native
    @JSName("emit")
    def emit_close(event: close): Boolean = js.native
    @JSName("emit")
    def emit_message(event: message, value: js.Any): Boolean = js.native
    @JSName("emit")
    def emit_messageerror(event: messageerror, error: js.Error): Boolean = js.native
    
    def off(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def off(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("off")
    def off_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("off")
    def off_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("off")
    def off_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    def on(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def on(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("on")
    def on_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("on")
    def on_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("on")
    def on_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    def once(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def once(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("once")
    def once_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("once")
    def once_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("once")
    def once_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    /**
      * Sends a JavaScript value to the receiving side of this channel.`value` is transferred in a way which is compatible with
      * the [HTML structured clone algorithm](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Structured_clone_algorithm).
      *
      * In particular, the significant differences to `JSON` are:
      *
      * * `value` may contain circular references.
      * * `value` may contain instances of builtin JS types such as `RegExp`s,`BigInt`s, `Map`s, `Set`s, etc.
      * * `value` may contain typed arrays, both using `ArrayBuffer`s
      * and `SharedArrayBuffer`s.
      * * `value` may contain [`WebAssembly.Module`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/WebAssembly/Module) instances.
      * * `value` may not contain native (C++-backed) objects other than:
      *
      * ```js
      * const { MessageChannel } = require('worker_threads');
      * const { port1, port2 } = new MessageChannel();
      *
      * port1.on('message', (message) => console.log(message));
      *
      * const circularData = {};
      * circularData.foo = circularData;
      * // Prints: { foo: [Circular] }
      * port2.postMessage(circularData);
      * ```
      *
      * `transferList` may be a list of [`ArrayBuffer`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/ArrayBuffer), `MessagePort` and `FileHandle` objects.
      * After transferring, they are not usable on the sending side of the channel
      * anymore (even if they are not contained in `value`). Unlike with `child processes`, transferring handles such as network sockets is currently
      * not supported.
      *
      * If `value` contains [`SharedArrayBuffer`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/SharedArrayBuffer) instances, those are accessible
      * from either thread. They cannot be listed in `transferList`.
      *
      * `value` may still contain `ArrayBuffer` instances that are not in`transferList`; in that case, the underlying memory is copied rather than moved.
      *
      * ```js
      * const { MessageChannel } = require('worker_threads');
      * const { port1, port2 } = new MessageChannel();
      *
      * port1.on('message', (message) => console.log(message));
      *
      * const uint8Array = new Uint8Array([ 1, 2, 3, 4 ]);
      * // This posts a copy of `uint8Array`:
      * port2.postMessage(uint8Array);
      * // This does not copy data, but renders `uint8Array` unusable:
      * port2.postMessage(uint8Array, [ uint8Array.buffer ]);
      *
      * // The memory for the `sharedUint8Array` is accessible from both the
      * // original and the copy received by `.on('message')`:
      * const sharedUint8Array = new Uint8Array(new SharedArrayBuffer(4));
      * port2.postMessage(sharedUint8Array);
      *
      * // This transfers a freshly created message port to the receiver.
      * // This can be used, for example, to create communication channels between
      * // multiple `Worker` threads that are children of the same parent thread.
      * const otherChannel = new MessageChannel();
      * port2.postMessage({ port: otherChannel.port1 }, [ otherChannel.port1 ]);
      * ```
      *
      * The message object is cloned immediately, and can be modified after
      * posting without having side effects.
      *
      * For more information on the serialization and deserialization mechanisms
      * behind this API, see the `serialization API of the v8 module`.
      * @since v10.5.0
      */
    def postMessage(value: js.Any): Unit = js.native
    def postMessage(value: js.Any, transferList: js.Array[TransferListItem]): Unit = js.native
    
    def prependListener(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def prependListener(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("prependListener")
    def prependListener_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("prependListener")
    def prependListener_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("prependListener")
    def prependListener_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    def prependOnceListener(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def prependOnceListener(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("prependOnceListener")
    def prependOnceListener_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("prependOnceListener")
    def prependOnceListener_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("prependOnceListener")
    def prependOnceListener_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    /**
      * Opposite of `unref()`. Calling `ref()` on a previously `unref()`ed port does_not_ let the program exit if it's the only active handle left (the default
      * behavior). If the port is `ref()`ed, calling `ref()` again has no effect.
      *
      * If listeners are attached or removed using `.on('message')`, the port
      * is `ref()`ed and `unref()`ed automatically depending on whether
      * listeners for the event exist.
      * @since v10.5.0
      */
    def ref(): Unit = js.native
    
    def removeListener(event: String, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    def removeListener(event: js.Symbol, listener: js.Function1[/* repeated */ js.Any, Unit]): this.type = js.native
    @JSName("removeListener")
    def removeListener_close(event: close, listener: js.Function0[Unit]): this.type = js.native
    @JSName("removeListener")
    def removeListener_message(event: message, listener: js.Function1[/* value */ js.Any, Unit]): this.type = js.native
    @JSName("removeListener")
    def removeListener_messageerror(event: messageerror, listener: js.Function1[/* error */ js.Error, Unit]): this.type = js.native
    
    /**
      * Starts receiving messages on this `MessagePort`. When using this port
      * as an event emitter, this is called automatically once `'message'`listeners are attached.
      *
      * This method exists for parity with the Web `MessagePort` API. In Node.js,
      * it is only useful for ignoring messages when no event listener is present.
      * Node.js also diverges in its handling of `.onmessage`. Setting it
      * automatically calls `.start()`, but unsetting it lets messages queue up
      * until a new handler is set or the port is discarded.
      * @since v10.5.0
      */
    def start(): Unit = js.native
    
    /**
      * Calling `unref()` on a port allows the thread to exit if this is the only
      * active handle in the event system. If the port is already `unref()`ed calling`unref()` again has no effect.
      *
      * If listeners are attached or removed using `.on('message')`, the port is`ref()`ed and `unref()`ed automatically depending on whether
      * listeners for the event exist.
      * @since v10.5.0
      */
    def unref(): Unit = js.native
  }
  
  /* Rewritten from type alias, can be one of: 
    - js.typedarray.ArrayBuffer
    - n3js.node.workerThreadsMod.MessagePort
    - n3js.node.fsPromisesMod.FileHandle
    - n3js.node.nodeCryptoMod.X509Certificate
    - n3js.node.nodeBufferMod.Blob
  */
  type TransferListItem = _TransferListItem | js.typedarray.ArrayBuffer
  
  trait _TransferListItem extends StObject
}
