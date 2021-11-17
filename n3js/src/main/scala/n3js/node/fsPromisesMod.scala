package n3js.node

import n3js.node.NodeJS.ArrayBufferView
import n3js.node.anon.BytesWritten
import n3js.node.anon.EncodingBufferEncoding
import n3js.node.anon.ObjectEncodingOptionsflagFlag
import n3js.node.anon.StatOptionsbigintfalseund
import n3js.node.anon.StatOptionsbiginttrue
import n3js.node.anon.`3`
import n3js.node.bufferMod.global.Buffer
import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.eventsMod.Abortable
import n3js.node.fsMod.BigIntStats
import n3js.node.fsMod.Mode
import n3js.node.fsMod.ObjectEncodingOptions
import n3js.node.fsMod.OpenMode
import n3js.node.fsMod.ReadVResult
import n3js.node.fsMod.StatOptions
import n3js.node.fsMod.WriteVResult
import n3js.node.nodeFsMod.ReadStream
import n3js.node.nodeFsMod.Stats
import n3js.node.nodeFsMod.WriteStream
import n3js.node.workerThreadsMod._TransferListItem
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object fsPromisesMod {
  
  @js.native
  trait CreateReadStreamOptions extends StObject {
    
    var autoClose: js.UndefOr[Boolean] = js.native
    
    var emitClose: js.UndefOr[Boolean] = js.native
    
    var encoding: js.UndefOr[BufferEncoding | Null] = js.native
    
    var end: js.UndefOr[Double] = js.native
    
    var highWaterMark: js.UndefOr[Double] = js.native
    
    var start: js.UndefOr[Double] = js.native
  }
  object CreateReadStreamOptions {
    
    @scala.inline
    def apply(): CreateReadStreamOptions = {
      val __obj = js.Dynamic.literal()
      __obj.asInstanceOf[CreateReadStreamOptions]
    }
    
    @scala.inline
    implicit class CreateReadStreamOptionsMutableBuilder[Self <: CreateReadStreamOptions] (val x: Self) extends AnyVal {
      
      @scala.inline
      def setAutoClose(value: Boolean): Self = StObject.set(x, "autoClose", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setAutoCloseUndefined: Self = StObject.set(x, "autoClose", js.undefined)
      
      @scala.inline
      def setEmitClose(value: Boolean): Self = StObject.set(x, "emitClose", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setEmitCloseUndefined: Self = StObject.set(x, "emitClose", js.undefined)
      
      @scala.inline
      def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setEncodingNull: Self = StObject.set(x, "encoding", null)
      
      @scala.inline
      def setEncodingUndefined: Self = StObject.set(x, "encoding", js.undefined)
      
      @scala.inline
      def setEnd(value: Double): Self = StObject.set(x, "end", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setEndUndefined: Self = StObject.set(x, "end", js.undefined)
      
      @scala.inline
      def setHighWaterMark(value: Double): Self = StObject.set(x, "highWaterMark", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setHighWaterMarkUndefined: Self = StObject.set(x, "highWaterMark", js.undefined)
      
      @scala.inline
      def setStart(value: Double): Self = StObject.set(x, "start", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setStartUndefined: Self = StObject.set(x, "start", js.undefined)
    }
  }
  
  @js.native
  trait CreateWriteStreamOptions extends StObject {
    
    var autoClose: js.UndefOr[Boolean] = js.native
    
    var emitClose: js.UndefOr[Boolean] = js.native
    
    var encoding: js.UndefOr[BufferEncoding | Null] = js.native
    
    var start: js.UndefOr[Double] = js.native
  }
  object CreateWriteStreamOptions {
    
    @scala.inline
    def apply(): CreateWriteStreamOptions = {
      val __obj = js.Dynamic.literal()
      __obj.asInstanceOf[CreateWriteStreamOptions]
    }
    
    @scala.inline
    implicit class CreateWriteStreamOptionsMutableBuilder[Self <: CreateWriteStreamOptions] (val x: Self) extends AnyVal {
      
      @scala.inline
      def setAutoClose(value: Boolean): Self = StObject.set(x, "autoClose", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setAutoCloseUndefined: Self = StObject.set(x, "autoClose", js.undefined)
      
      @scala.inline
      def setEmitClose(value: Boolean): Self = StObject.set(x, "emitClose", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setEmitCloseUndefined: Self = StObject.set(x, "emitClose", js.undefined)
      
      @scala.inline
      def setEncoding(value: BufferEncoding): Self = StObject.set(x, "encoding", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setEncodingNull: Self = StObject.set(x, "encoding", null)
      
      @scala.inline
      def setEncodingUndefined: Self = StObject.set(x, "encoding", js.undefined)
      
      @scala.inline
      def setStart(value: Double): Self = StObject.set(x, "start", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setStartUndefined: Self = StObject.set(x, "start", js.undefined)
    }
  }
  
  // TODO: Add `EventEmitter` close
  @js.native
  trait FileHandle
    extends StObject
       with _TransferListItem {
    
    /**
      * Alias of `filehandle.writeFile()`.
      *
      * When operating on file handles, the mode cannot be changed from what it was set
      * to with `fsPromises.open()`. Therefore, this is equivalent to `filehandle.writeFile()`.
      * @since v10.0.0
      * @return Fulfills with `undefined` upon success.
      */
    def appendFile(data: String): js.Promise[Unit] = js.native
    def appendFile(data: String, options: ObjectEncodingOptions with FlagAndOpenMode): js.Promise[Unit] = js.native
    def appendFile(data: String, options: BufferEncoding): js.Promise[Unit] = js.native
    def appendFile(data: js.typedarray.Uint8Array): js.Promise[Unit] = js.native
    def appendFile(data: js.typedarray.Uint8Array, options: ObjectEncodingOptions with FlagAndOpenMode): js.Promise[Unit] = js.native
    def appendFile(data: js.typedarray.Uint8Array, options: BufferEncoding): js.Promise[Unit] = js.native
    
    /**
      * Modifies the permissions on the file. See [`chmod(2)`](http://man7.org/linux/man-pages/man2/chmod.2.html).
      * @since v10.0.0
      * @param mode the file mode bit mask.
      * @return Fulfills with `undefined` upon success.
      */
    def chmod(mode: Mode): js.Promise[Unit] = js.native
    
    /**
      * Changes the ownership of the file. A wrapper for [`chown(2)`](http://man7.org/linux/man-pages/man2/chown.2.html).
      * @since v10.0.0
      * @param uid The file's new owner's user id.
      * @param gid The file's new group's group id.
      * @return Fulfills with `undefined` upon success.
      */
    def chown(uid: Double, gid: Double): js.Promise[Unit] = js.native
    
    /**
      * Closes the file handle after waiting for any pending operation on the handle to
      * complete.
      *
      * ```js
      * import { open } from 'fs/promises';
      *
      * let filehandle;
      * try {
      *   filehandle = await open('thefile.txt', 'r');
      * } finally {
      *   await filehandle?.close();
      * }
      * ```
      * @since v10.0.0
      * @return Fulfills with `undefined` upon success.
      */
    def close(): js.Promise[Unit] = js.native
    
    /**
      * Unlike the 16 kb default `highWaterMark` for a `stream.Readable`, the stream
      * returned by this method has a default `highWaterMark` of 64 kb.
      *
      * `options` can include `start` and `end` values to read a range of bytes from
      * the file instead of the entire file. Both `start` and `end` are inclusive and
      * start counting at 0, allowed values are in the
      * \[0, [`Number.MAX_SAFE_INTEGER`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/MAX_SAFE_INTEGER)\] range. If `start` is
      * omitted or `undefined`, `filehandle.createReadStream()` reads sequentially from
      * the current file position. The `encoding` can be any one of those accepted by `Buffer`.
      *
      * If the `FileHandle` points to a character device that only supports blocking
      * reads (such as keyboard or sound card), read operations do not finish until data
      * is available. This can prevent the process from exiting and the stream from
      * closing naturally.
      *
      * By default, the stream will emit a `'close'` event after it has been
      * destroyed.  Set the `emitClose` option to `false` to change this behavior.
      *
      * ```js
      * import { open } from 'fs/promises';
      *
      * const fd = await open('/dev/input/event0');
      * // Create a stream from some character device.
      * const stream = fd.createReadStream();
      * setTimeout(() => {
      *   stream.close(); // This may not close the stream.
      *   // Artificially marking end-of-stream, as if the underlying resource had
      *   // indicated end-of-file by itself, allows the stream to close.
      *   // This does not cancel pending read operations, and if there is such an
      *   // operation, the process may still not be able to exit successfully
      *   // until it finishes.
      *   stream.push(null);
      *   stream.read(0);
      * }, 100);
      * ```
      *
      * If `autoClose` is false, then the file descriptor won't be closed, even if
      * there's an error. It is the application's responsibility to close it and make
      * sure there's no file descriptor leak. If `autoClose` is set to true (default
      * behavior), on `'error'` or `'end'` the file descriptor will be closed
      * automatically.
      *
      * An example to read the last 10 bytes of a file which is 100 bytes long:
      *
      * ```js
      * import { open } from 'fs/promises';
      *
      * const fd = await open('sample.txt');
      * fd.createReadStream({ start: 90, end: 99 });
      * ```
      * @since v16.11.0
      */
    def createReadStream(): ReadStream = js.native
    def createReadStream(options: CreateReadStreamOptions): ReadStream = js.native
    
    /**
      * `options` may also include a `start` option to allow writing data at some
      * position past the beginning of the file, allowed values are in the
      * \[0, [`Number.MAX_SAFE_INTEGER`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/MAX_SAFE_INTEGER)\] range. Modifying a file rather than replacing
      * it may require the `flags` `open` option to be set to `r+` rather than the
      * default `r`. The `encoding` can be any one of those accepted by `Buffer`.
      *
      * If `autoClose` is set to true (default behavior) on `'error'` or `'finish'`the file descriptor will be closed automatically. If `autoClose` is false,
      * then the file descriptor won't be closed, even if there's an error.
      * It is the application's responsibility to close it and make sure there's no
      * file descriptor leak.
      *
      * By default, the stream will emit a `'close'` event after it has been
      * destroyed.  Set the `emitClose` option to `false` to change this behavior.
      * @since v16.11.0
      */
    def createWriteStream(): WriteStream = js.native
    def createWriteStream(options: CreateWriteStreamOptions): WriteStream = js.native
    
    /**
      * Forces all currently queued I/O operations associated with the file to the
      * operating system's synchronized I/O completion state. Refer to the POSIX [`fdatasync(2)`](http://man7.org/linux/man-pages/man2/fdatasync.2.html) documentation for details.
      *
      * Unlike `filehandle.sync` this method does not flush modified metadata.
      * @since v10.0.0
      * @return Fulfills with `undefined` upon success.
      */
    def datasync(): js.Promise[Unit] = js.native
    
    /**
      * The numeric file descriptor managed by the {FileHandle} object.
      * @since v10.0.0
      */
    val fd: Double = js.native
    
    def read[T /* <: js.typedarray.ArrayBufferView */](): js.Promise[FileReadResult[T]] = js.native
    /**
      * Reads data from the file and stores that in the given buffer.
      *
      * If the file is not modified concurrently, the end-of-file is reached when the
      * number of bytes read is zero.
      * @since v10.0.0
      * @param buffer A buffer that will be filled with the file data read.
      * @param offset The location in the buffer at which to start filling.
      * @param length The number of bytes to read.
      * @param position The location where to begin reading data from the file. If `null`, data will be read from the current file position, and the position will be updated. If `position` is an
      * integer, the current file position will remain unchanged.
      * @return Fulfills upon success with an object with two properties:
      */
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Double, length: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Double, length: Double, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Double, length: Null, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Double, length: Unit, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Null, length: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Null, length: Double, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Null, length: Null, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Null, length: Unit, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Unit, length: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Unit, length: Double, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Unit, length: Null, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](buffer: T, offset: Unit, length: Unit, position: Double): js.Promise[FileReadResult[T]] = js.native
    def read[T /* <: js.typedarray.ArrayBufferView */](options: FileReadOptions[T]): js.Promise[FileReadResult[T]] = js.native
    
    /**
      * Asynchronously reads the entire contents of a file.
      *
      * If `options` is a string, then it specifies the `encoding`.
      *
      * The `FileHandle` has to support reading.
      *
      * If one or more `filehandle.read()` calls are made on a file handle and then a`filehandle.readFile()` call is made, the data will be read from the current
      * position till the end of the file. It doesn't always read from the beginning
      * of the file.
      * @since v10.0.0
      * @return Fulfills upon a successful read with the contents of the file. If no encoding is specified (using `options.encoding`), the data is returned as a {Buffer} object. Otherwise, the
      * data will be a string.
      */
    /**
      * Asynchronously reads the entire contents of a file. The underlying file will _not_ be closed automatically.
      * The `FileHandle` must have been opened for reading.
      * @param options An object that may contain an optional flag.
      * If a flag is not provided, it defaults to `'r'`.
      */
    def readFile(): js.Promise[Buffer] = js.native
    /**
      * Asynchronously reads the entire contents of a file. The underlying file will _not_ be closed automatically.
      * The `FileHandle` must have been opened for reading.
      * @param options An object that may contain an optional flag.
      * If a flag is not provided, it defaults to `'r'`.
      */
    def readFile(options: EncodingBufferEncoding): js.Promise[String] = js.native
    def readFile(options: ObjectEncodingOptionsflagFlag): js.Promise[String | Buffer] = js.native
    def readFile(options: `3`): js.Promise[Buffer] = js.native
    def readFile(options: BufferEncoding): js.Promise[String] = js.native
    
    /**
      * Read from a file and write to an array of [ArrayBufferView](https://developer.mozilla.org/en-US/docs/Web/API/ArrayBufferView) s
      * @since v13.13.0, v12.17.0
      * @param position The offset from the beginning of the file where the data should be read from. If `position` is not a `number`, the data will be read from the current position.
      * @return Fulfills upon success an object containing two properties:
      */
    def readv(buffers: js.Array[ArrayBufferView]): js.Promise[ReadVResult] = js.native
    def readv(buffers: js.Array[ArrayBufferView], position: Double): js.Promise[ReadVResult] = js.native
    
    /**
      * @since v10.0.0
      * @return Fulfills with an {fs.Stats} for the file.
      */
    def stat(): js.Promise[Stats] = js.native
    def stat(opts: StatOptionsbigintfalseund): js.Promise[Stats] = js.native
    def stat(opts: StatOptionsbiginttrue): js.Promise[BigIntStats] = js.native
    def stat(opts: StatOptions): js.Promise[Stats | BigIntStats] = js.native
    
    /**
      * Request that all data for the open file descriptor is flushed to the storage
      * device. The specific implementation is operating system and device specific.
      * Refer to the POSIX [`fsync(2)`](http://man7.org/linux/man-pages/man2/fsync.2.html) documentation for more detail.
      * @since v10.0.0
      * @return Fufills with `undefined` upon success.
      */
    def sync(): js.Promise[Unit] = js.native
    
    /**
      * Truncates the file.
      *
      * If the file was larger than `len` bytes, only the first `len` bytes will be
      * retained in the file.
      *
      * The following example retains only the first four bytes of the file:
      *
      * ```js
      * import { open } from 'fs/promises';
      *
      * let filehandle = null;
      * try {
      *   filehandle = await open('temp.txt', 'r+');
      *   await filehandle.truncate(4);
      * } finally {
      *   await filehandle?.close();
      * }
      * ```
      *
      * If the file previously was shorter than `len` bytes, it is extended, and the
      * extended part is filled with null bytes (`'\0'`):
      *
      * If `len` is negative then `0` will be used.
      * @since v10.0.0
      * @param [len=0]
      * @return Fulfills with `undefined` upon success.
      */
    def truncate(): js.Promise[Unit] = js.native
    def truncate(len: Double): js.Promise[Unit] = js.native
    
    /**
      * Change the file system timestamps of the object referenced by the `FileHandle` then resolves the promise with no arguments upon success.
      * @since v10.0.0
      */
    def utimes(atime: String, mtime: String): js.Promise[Unit] = js.native
    def utimes(atime: String, mtime: js.Date): js.Promise[Unit] = js.native
    def utimes(atime: String, mtime: Double): js.Promise[Unit] = js.native
    def utimes(atime: js.Date, mtime: String): js.Promise[Unit] = js.native
    def utimes(atime: js.Date, mtime: js.Date): js.Promise[Unit] = js.native
    def utimes(atime: js.Date, mtime: Double): js.Promise[Unit] = js.native
    def utimes(atime: Double, mtime: String): js.Promise[Unit] = js.native
    def utimes(atime: Double, mtime: js.Date): js.Promise[Unit] = js.native
    def utimes(atime: Double, mtime: Double): js.Promise[Unit] = js.native
    
    def write(data: String): js.Promise[BytesWritten] = js.native
    def write(data: String, position: Double): js.Promise[BytesWritten] = js.native
    def write(data: String, position: Double, encoding: BufferEncoding): js.Promise[BytesWritten] = js.native
    def write(data: String, position: Null, encoding: BufferEncoding): js.Promise[BytesWritten] = js.native
    def write(data: String, position: Unit, encoding: BufferEncoding): js.Promise[BytesWritten] = js.native
    /**
      * Write `buffer` to the file.
      *
      * If `buffer` is a plain object, it must have an own (not inherited) `toString`function property.
      *
      * The promise is resolved with an object containing two properties:
      *
      * It is unsafe to use `filehandle.write()` multiple times on the same file
      * without waiting for the promise to be resolved (or rejected). For this
      * scenario, use `fs.createWriteStream()`.
      *
      * On Linux, positional writes do not work when the file is opened in append mode.
      * The kernel ignores the position argument and always appends the data to
      * the end of the file.
      * @since v10.0.0
      * @param [offset=0] The start position from within `buffer` where the data to write begins.
      * @param [length=buffer.byteLength] The number of bytes from `buffer` to write.
      * @param position The offset from the beginning of the file where the data from `buffer` should be written. If `position` is not a `number`, the data will be written at the current position.
      * See the POSIX pwrite(2) documentation for more detail.
      */
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Double, length: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Double, length: Double, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Double, length: Null, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Double, length: Unit, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Null, length: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Null, length: Double, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Null, length: Null, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Null, length: Unit, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Unit, length: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Unit, length: Double, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Unit, length: Null, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    def write[TBuffer /* <: js.typedarray.Uint8Array */](buffer: TBuffer, offset: Unit, length: Unit, position: Double): js.Promise[n3js.node.anon.Buffer[TBuffer]] = js.native
    
    /**
      * Asynchronously writes data to a file, replacing the file if it already exists.`data` can be a string, a buffer, an
      * [AsyncIterable](https://tc39.github.io/ecma262/#sec-asynciterable-interface) or
      * [Iterable](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Iteration_protocols#The_iterable_protocol) object, or an
      * object with an own `toString` function
      * property. The promise is resolved with no arguments upon success.
      *
      * If `options` is a string, then it specifies the `encoding`.
      *
      * The `FileHandle` has to support writing.
      *
      * It is unsafe to use `filehandle.writeFile()` multiple times on the same file
      * without waiting for the promise to be resolved (or rejected).
      *
      * If one or more `filehandle.write()` calls are made on a file handle and then a`filehandle.writeFile()` call is made, the data will be written from the
      * current position till the end of the file. It doesn't always write from the
      * beginning of the file.
      * @since v10.0.0
      */
    def writeFile(data: String): js.Promise[Unit] = js.native
    def writeFile(data: String, options: ObjectEncodingOptions with FlagAndOpenMode with Abortable): js.Promise[Unit] = js.native
    def writeFile(data: String, options: BufferEncoding): js.Promise[Unit] = js.native
    def writeFile(data: js.typedarray.Uint8Array): js.Promise[Unit] = js.native
    def writeFile(data: js.typedarray.Uint8Array, options: ObjectEncodingOptions with FlagAndOpenMode with Abortable): js.Promise[Unit] = js.native
    def writeFile(data: js.typedarray.Uint8Array, options: BufferEncoding): js.Promise[Unit] = js.native
    
    /**
      * Write an array of [ArrayBufferView](https://developer.mozilla.org/en-US/docs/Web/API/ArrayBufferView) s to the file.
      *
      * The promise is resolved with an object containing a two properties:
      *
      * It is unsafe to call `writev()` multiple times on the same file without waiting
      * for the promise to be resolved (or rejected).
      *
      * On Linux, positional writes don't work when the file is opened in append mode.
      * The kernel ignores the position argument and always appends the data to
      * the end of the file.
      * @since v12.9.0
      * @param position The offset from the beginning of the file where the data from `buffers` should be written. If `position` is not a `number`, the data will be written at the current
      * position.
      */
    def writev(buffers: js.Array[ArrayBufferView]): js.Promise[WriteVResult] = js.native
    def writev(buffers: js.Array[ArrayBufferView], position: Double): js.Promise[WriteVResult] = js.native
  }
  
  @js.native
  trait FileReadOptions[T /* <: js.typedarray.ArrayBufferView */] extends StObject {
    
    /**
      * @default `Buffer.alloc(0xffff)`
      */
    var buffer: js.UndefOr[T] = js.native
    
    /**
      * @default `buffer.byteLength`
      */
    var length: js.UndefOr[Double | Null] = js.native
    
    /**
      * @default 0
      */
    var offset: js.UndefOr[Double | Null] = js.native
    
    var position: js.UndefOr[Double | Null] = js.native
  }
  object FileReadOptions {
    
    @scala.inline
    def apply[T /* <: js.typedarray.ArrayBufferView */](): FileReadOptions[T] = {
      val __obj = js.Dynamic.literal()
      __obj.asInstanceOf[FileReadOptions[T]]
    }
    
    @scala.inline
    implicit class FileReadOptionsMutableBuilder[Self <: FileReadOptions[_], T /* <: js.typedarray.ArrayBufferView */] (val x: Self with FileReadOptions[T]) extends AnyVal {
      
      @scala.inline
      def setBuffer(value: T): Self = StObject.set(x, "buffer", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setBufferUndefined: Self = StObject.set(x, "buffer", js.undefined)
      
      @scala.inline
      def setLength(value: Double): Self = StObject.set(x, "length", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setLengthNull: Self = StObject.set(x, "length", null)
      
      @scala.inline
      def setLengthUndefined: Self = StObject.set(x, "length", js.undefined)
      
      @scala.inline
      def setOffset(value: Double): Self = StObject.set(x, "offset", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setOffsetNull: Self = StObject.set(x, "offset", null)
      
      @scala.inline
      def setOffsetUndefined: Self = StObject.set(x, "offset", js.undefined)
      
      @scala.inline
      def setPosition(value: Double): Self = StObject.set(x, "position", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setPositionNull: Self = StObject.set(x, "position", null)
      
      @scala.inline
      def setPositionUndefined: Self = StObject.set(x, "position", js.undefined)
    }
  }
  
  @js.native
  trait FileReadResult[T /* <: js.typedarray.ArrayBufferView */] extends StObject {
    
    var buffer: T = js.native
    
    var bytesRead: Double = js.native
  }
  object FileReadResult {
    
    @scala.inline
    def apply[T /* <: js.typedarray.ArrayBufferView */](buffer: T, bytesRead: Double): FileReadResult[T] = {
      val __obj = js.Dynamic.literal(buffer = buffer.asInstanceOf[js.Any], bytesRead = bytesRead.asInstanceOf[js.Any])
      __obj.asInstanceOf[FileReadResult[T]]
    }
    
    @scala.inline
    implicit class FileReadResultMutableBuilder[Self <: FileReadResult[_], T /* <: js.typedarray.ArrayBufferView */] (val x: Self with FileReadResult[T]) extends AnyVal {
      
      @scala.inline
      def setBuffer(value: T): Self = StObject.set(x, "buffer", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setBytesRead(value: Double): Self = StObject.set(x, "bytesRead", value.asInstanceOf[js.Any])
    }
  }
  
  @js.native
  trait FlagAndOpenMode extends StObject {
    
    var flag: js.UndefOr[OpenMode] = js.native
    
    var mode: js.UndefOr[Mode] = js.native
  }
  object FlagAndOpenMode {
    
    @scala.inline
    def apply(): FlagAndOpenMode = {
      val __obj = js.Dynamic.literal()
      __obj.asInstanceOf[FlagAndOpenMode]
    }
    
    @scala.inline
    implicit class FlagAndOpenModeMutableBuilder[Self <: FlagAndOpenMode] (val x: Self) extends AnyVal {
      
      @scala.inline
      def setFlag(value: OpenMode): Self = StObject.set(x, "flag", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setFlagUndefined: Self = StObject.set(x, "flag", js.undefined)
      
      @scala.inline
      def setMode(value: Mode): Self = StObject.set(x, "mode", value.asInstanceOf[js.Any])
      
      @scala.inline
      def setModeUndefined: Self = StObject.set(x, "mode", js.undefined)
    }
  }
}
