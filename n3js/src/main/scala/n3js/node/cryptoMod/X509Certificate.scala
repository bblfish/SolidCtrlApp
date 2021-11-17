package n3js.node.cryptoMod

import n3js.node.bufferMod.global.Buffer
import n3js.node.tlsMod.PeerCertificate
import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

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
@JSImport("crypto", "X509Certificate")
@js.native
class X509Certificate protected () extends StObject {
  def this(buffer: BinaryLike) = this()
  
  /**
    * Will be \`true\` if this is a Certificate Authority (ca) certificate.
    * @since v15.6.0
    */
  val ca: Boolean = js.native
  
  /**
    * Checks whether the certificate matches the given email address.
    * @since v15.6.0
    * @return Returns `email` if the certificate matches, `undefined` if it does not.
    */
  def checkEmail(email: String): js.UndefOr[String] = js.native
  def checkEmail(email: String, options: X509CheckOptions): js.UndefOr[String] = js.native
  
  /**
    * Checks whether the certificate matches the given host name.
    * @since v15.6.0
    * @return Returns `name` if the certificate matches, `undefined` if it does not.
    */
  def checkHost(name: String): js.UndefOr[String] = js.native
  def checkHost(name: String, options: X509CheckOptions): js.UndefOr[String] = js.native
  
  /**
    * Checks whether the certificate matches the given IP address (IPv4 or IPv6).
    * @since v15.6.0
    * @return Returns `ip` if the certificate matches, `undefined` if it does not.
    */
  def checkIP(ip: String): js.UndefOr[String] = js.native
  def checkIP(ip: String, options: X509CheckOptions): js.UndefOr[String] = js.native
  
  /**
    * Checks whether this certificate was issued by the given `otherCert`.
    * @since v15.6.0
    */
  def checkIssued(otherCert: X509Certificate): Boolean = js.native
  
  /**
    * Checks whether the public key for this certificate is consistent with
    * the given private key.
    * @since v15.6.0
    * @param privateKey A private key.
    */
  def checkPrivateKey(privateKey: KeyObject): Boolean = js.native
  
  /**
    * The SHA-1 fingerprint of this certificate.
    * @since v15.6.0
    */
  val fingerprint: String = js.native
  
  /**
    * The SHA-256 fingerprint of this certificate.
    * @since v15.6.0
    */
  val fingerprint256: String = js.native
  
  /**
    * The information access content of this certificate.
    * @since v15.6.0
    */
  val infoAccess: String = js.native
  
  /**
    * The issuer identification included in this certificate.
    * @since v15.6.0
    */
  val issuer: String = js.native
  
  /**
    * The issuer certificate or `undefined` if the issuer certificate is not
    * available.
    * @since v15.9.0
    */
  val issuerCertificate: js.UndefOr[X509Certificate] = js.native
  
  /**
    * An array detailing the key usages for this certificate.
    * @since v15.6.0
    */
  val keyUsage: js.Array[String] = js.native
  
  /**
    * The public key `KeyObject` for this certificate.
    * @since v15.6.0
    */
  val publicKey: KeyObject = js.native
  
  /**
    * A `Buffer` containing the DER encoding of this certificate.
    * @since v15.6.0
    */
  val raw: Buffer = js.native
  
  /**
    * The serial number of this certificate.
    * @since v15.6.0
    */
  val serialNumber: String = js.native
  
  /**
    * The complete subject of this certificate.
    * @since v15.6.0
    */
  val subject: String = js.native
  
  /**
    * The subject alternative name specified for this certificate.
    * @since v15.6.0
    */
  val subjectAltName: String = js.native
  
  /**
    * There is no standard JSON encoding for X509 certificates. The`toJSON()` method returns a string containing the PEM encoded
    * certificate.
    * @since v15.6.0
    */
  def toJSON(): String = js.native
  
  /**
    * Returns information about this certificate using the legacy `certificate object` encoding.
    * @since v15.6.0
    */
  def toLegacyObject(): PeerCertificate = js.native
  
  /**
    * The date/time from which this certificate is considered valid.
    * @since v15.6.0
    */
  val validFrom: String = js.native
  
  /**
    * The date/time until which this certificate is considered valid.
    * @since v15.6.0
    */
  val validTo: String = js.native
  
  /**
    * Verifies that this certificate was signed by the given public key.
    * Does not perform any other validation checks on the certificate.
    * @since v15.6.0
    * @param publicKey A public key.
    */
  def verify(publicKey: KeyObject): Boolean = js.native
}
