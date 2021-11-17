package n3js.node

import n3js.node.bufferMod.TranscodeEncoding
import n3js.node.bufferMod.global.BufferEncoding
import n3js.node.childProcessMod.IOType
import n3js.node.childProcessMod.StdioNull
import n3js.node.childProcessMod.StdioPipeNamed
import n3js.node.cryptoMod.BinaryToTextEncoding
import n3js.node.cryptoMod.CharacterEncoding
import n3js.node.cryptoMod.DSAEncoding
import n3js.node.cryptoMod.Encoding
import n3js.node.cryptoMod.KeyFormat
import n3js.node.cryptoMod.KeyObjectType
import n3js.node.cryptoMod.KeyType
import n3js.node.cryptoMod.LegacyCharacterEncoding
import n3js.node.utilMod.Style
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

object nodeStrings {
  
  @js.native
  sealed trait Buffer extends StObject
  @scala.inline
  def Buffer: Buffer = "Buffer".asInstanceOf[Buffer]
  
  @js.native
  sealed trait always extends StObject
  @scala.inline
  def always: always = "always".asInstanceOf[always]
  
  @js.native
  sealed trait ascii
    extends StObject
       with BufferEncoding
       with Encoding
       with LegacyCharacterEncoding
       with TranscodeEncoding
  @scala.inline
  def ascii: ascii = "ascii".asInstanceOf[ascii]
  
  @js.native
  sealed trait base64
    extends StObject
       with BinaryToTextEncoding
       with BufferEncoding
       with Encoding
  @scala.inline
  def base64: base64 = "base64".asInstanceOf[base64]
  
  @js.native
  sealed trait base64url
    extends StObject
       with BinaryToTextEncoding
       with BufferEncoding
       with Encoding
  @scala.inline
  def base64url: base64url = "base64url".asInstanceOf[base64url]
  
  @js.native
  sealed trait bigint
    extends StObject
       with Style
  @scala.inline
  def bigint: bigint = "bigint".asInstanceOf[bigint]
  
  @js.native
  sealed trait binary
    extends StObject
       with BufferEncoding
       with Encoding
       with LegacyCharacterEncoding
       with TranscodeEncoding
  @scala.inline
  def binary: binary = "binary".asInstanceOf[binary]
  
  @js.native
  sealed trait boolean
    extends StObject
       with Style
  @scala.inline
  def boolean: boolean = "boolean".asInstanceOf[boolean]
  
  @js.native
  sealed trait close extends StObject
  @scala.inline
  def close: close = "close".asInstanceOf[close]
  
  @js.native
  sealed trait data extends StObject
  @scala.inline
  def data: data = "data".asInstanceOf[data]
  
  @js.native
  sealed trait date
    extends StObject
       with Style
  @scala.inline
  def date: date = "date".asInstanceOf[date]
  
  @js.native
  sealed trait der
    extends StObject
       with DSAEncoding
       with KeyFormat
  @scala.inline
  def der: der = "der".asInstanceOf[der]
  
  @js.native
  sealed trait drain extends StObject
  @scala.inline
  def drain: drain = "drain".asInstanceOf[drain]
  
  @js.native
  sealed trait dsa
    extends StObject
       with KeyType
  @scala.inline
  def dsa: dsa = "dsa".asInstanceOf[dsa]
  
  @js.native
  sealed trait ec
    extends StObject
       with KeyType
  @scala.inline
  def ec: ec = "ec".asInstanceOf[ec]
  
  @js.native
  sealed trait ed25519
    extends StObject
       with KeyType
  @scala.inline
  def ed25519: ed25519 = "ed25519".asInstanceOf[ed25519]
  
  @js.native
  sealed trait ed448
    extends StObject
       with KeyType
  @scala.inline
  def ed448: ed448 = "ed448".asInstanceOf[ed448]
  
  @js.native
  sealed trait end extends StObject
  @scala.inline
  def end: end = "end".asInstanceOf[end]
  
  @js.native
  sealed trait error extends StObject
  @scala.inline
  def error: error = "error".asInstanceOf[error]
  
  @js.native
  sealed trait finish extends StObject
  @scala.inline
  def finish: finish = "finish".asInstanceOf[finish]
  
  @js.native
  sealed trait hex
    extends StObject
       with BinaryToTextEncoding
       with BufferEncoding
       with Encoding
  @scala.inline
  def hex: hex = "hex".asInstanceOf[hex]
  
  @js.native
  sealed trait `ieee-p1363`
    extends StObject
       with DSAEncoding
  @scala.inline
  def `ieee-p1363`: `ieee-p1363` = "ieee-p1363".asInstanceOf[`ieee-p1363`]
  
  @js.native
  sealed trait ignore
    extends StObject
       with IOType
       with StdioNull
  @scala.inline
  def ignore: ignore = "ignore".asInstanceOf[ignore]
  
  @js.native
  sealed trait inherit
    extends StObject
       with IOType
       with StdioNull
  @scala.inline
  def inherit: inherit = "inherit".asInstanceOf[inherit]
  
  @js.native
  sealed trait jwk extends StObject
  @scala.inline
  def jwk: jwk = "jwk".asInstanceOf[jwk]
  
  @js.native
  sealed trait latin1
    extends StObject
       with BufferEncoding
       with CharacterEncoding
       with Encoding
       with TranscodeEncoding
  @scala.inline
  def latin1: latin1 = "latin1".asInstanceOf[latin1]
  
  @js.native
  sealed trait message extends StObject
  @scala.inline
  def message: message = "message".asInstanceOf[message]
  
  @js.native
  sealed trait messageerror extends StObject
  @scala.inline
  def messageerror: messageerror = "messageerror".asInstanceOf[messageerror]
  
  @js.native
  sealed trait module
    extends StObject
       with Style
  @scala.inline
  def module: module = "module".asInstanceOf[module]
  
  @js.native
  sealed trait never extends StObject
  @scala.inline
  def never: never = "never".asInstanceOf[never]
  
  @js.native
  sealed trait `null`
    extends StObject
       with Style
  @scala.inline
  def `null`: `null` = "null".asInstanceOf[`null`]
  
  @js.native
  sealed trait number
    extends StObject
       with Style
  @scala.inline
  def number: number = "number".asInstanceOf[number]
  
  @js.native
  sealed trait overlapped
    extends StObject
       with IOType
       with StdioPipeNamed
  @scala.inline
  def overlapped: overlapped = "overlapped".asInstanceOf[overlapped]
  
  @js.native
  sealed trait pause extends StObject
  @scala.inline
  def pause: pause = "pause".asInstanceOf[pause]
  
  @js.native
  sealed trait pem
    extends StObject
       with KeyFormat
  @scala.inline
  def pem: pem = "pem".asInstanceOf[pem]
  
  @js.native
  sealed trait pipe
    extends StObject
       with IOType
       with StdioPipeNamed
  @scala.inline
  def pipe: pipe = "pipe".asInstanceOf[pipe]
  
  @js.native
  sealed trait pkcs1 extends StObject
  @scala.inline
  def pkcs1: pkcs1 = "pkcs1".asInstanceOf[pkcs1]
  
  @js.native
  sealed trait pkcs8 extends StObject
  @scala.inline
  def pkcs8: pkcs8 = "pkcs8".asInstanceOf[pkcs8]
  
  @js.native
  sealed trait `private`
    extends StObject
       with KeyObjectType
  @scala.inline
  def `private`: `private` = "private".asInstanceOf[`private`]
  
  @js.native
  sealed trait public
    extends StObject
       with KeyObjectType
  @scala.inline
  def public: public = "public".asInstanceOf[public]
  
  @js.native
  sealed trait readable extends StObject
  @scala.inline
  def readable: readable = "readable".asInstanceOf[readable]
  
  @js.native
  sealed trait regexp
    extends StObject
       with Style
  @scala.inline
  def regexp: regexp = "regexp".asInstanceOf[regexp]
  
  @js.native
  sealed trait resume extends StObject
  @scala.inline
  def resume: resume = "resume".asInstanceOf[resume]
  
  @js.native
  sealed trait rsa
    extends StObject
       with KeyType
  @scala.inline
  def rsa: rsa = "rsa".asInstanceOf[rsa]
  
  @js.native
  sealed trait `rsa-pss`
    extends StObject
       with KeyType
  @scala.inline
  def `rsa-pss`: `rsa-pss` = "rsa-pss".asInstanceOf[`rsa-pss`]
  
  @js.native
  sealed trait sec1 extends StObject
  @scala.inline
  def sec1: sec1 = "sec1".asInstanceOf[sec1]
  
  @js.native
  sealed trait secret
    extends StObject
       with KeyObjectType
  @scala.inline
  def secret: secret = "secret".asInstanceOf[secret]
  
  @js.native
  sealed trait special
    extends StObject
       with Style
  @scala.inline
  def special: special = "special".asInstanceOf[special]
  
  @js.native
  sealed trait spki extends StObject
  @scala.inline
  def spki: spki = "spki".asInstanceOf[spki]
  
  @js.native
  sealed trait string
    extends StObject
       with Style
  @scala.inline
  def string: string = "string".asInstanceOf[string]
  
  @js.native
  sealed trait symbol
    extends StObject
       with Style
  @scala.inline
  def symbol: symbol = "symbol".asInstanceOf[symbol]
  
  @js.native
  sealed trait `ucs-2`
    extends StObject
       with BufferEncoding
       with Encoding
       with LegacyCharacterEncoding
  @scala.inline
  def `ucs-2`: `ucs-2` = "ucs-2".asInstanceOf[`ucs-2`]
  
  @js.native
  sealed trait ucs2
    extends StObject
       with BufferEncoding
       with Encoding
       with LegacyCharacterEncoding
       with TranscodeEncoding
  @scala.inline
  def ucs2: ucs2 = "ucs2".asInstanceOf[ucs2]
  
  @js.native
  sealed trait undefined
    extends StObject
       with Style
  @scala.inline
  def undefined: undefined = "undefined".asInstanceOf[undefined]
  
  @js.native
  sealed trait unpipe extends StObject
  @scala.inline
  def unpipe: unpipe = "unpipe".asInstanceOf[unpipe]
  
  @js.native
  sealed trait `utf-8`
    extends StObject
       with BufferEncoding
       with CharacterEncoding
       with Encoding
  @scala.inline
  def `utf-8`: `utf-8` = "utf-8".asInstanceOf[`utf-8`]
  
  @js.native
  sealed trait utf16le
    extends StObject
       with BufferEncoding
       with CharacterEncoding
       with Encoding
       with TranscodeEncoding
  @scala.inline
  def utf16le: utf16le = "utf16le".asInstanceOf[utf16le]
  
  @js.native
  sealed trait utf8
    extends StObject
       with BufferEncoding
       with CharacterEncoding
       with Encoding
       with TranscodeEncoding
  @scala.inline
  def utf8: utf8 = "utf8".asInstanceOf[utf8]
  
  @js.native
  sealed trait x25519
    extends StObject
       with KeyType
  @scala.inline
  def x25519: x25519 = "x25519".asInstanceOf[x25519]
  
  @js.native
  sealed trait x448
    extends StObject
       with KeyType
  @scala.inline
  def x448: x448 = "x448".asInstanceOf[x448]
}
