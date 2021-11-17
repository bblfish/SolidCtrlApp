package n3js.node.cryptoMod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

@js.native
trait AsymmetricKeyDetails extends StObject {
  
  /**
    * Size of q in bits (DSA).
    */
  var divisorLength: js.UndefOr[Double] = js.native
  
  /**
    * Name of the message digest (RSA-PSS).
    */
  var hashAlgorithm: js.UndefOr[String] = js.native
  
  /**
    * Name of the message digest used by MGF1 (RSA-PSS).
    */
  var mgf1HashAlgorithm: js.UndefOr[String] = js.native
  
  /**
    * Key size in bits (RSA, DSA).
    */
  var modulusLength: js.UndefOr[Double] = js.native
  
  /**
    * Name of the curve (EC).
    */
  var namedCurve: js.UndefOr[String] = js.native
  
  /**
    * Public exponent (RSA).
    */
  var publicExponent: js.UndefOr[js.BigInt] = js.native
  
  /**
    * Minimal salt length in bytes (RSA-PSS).
    */
  var saltLength: js.UndefOr[Double] = js.native
}
object AsymmetricKeyDetails {
  
  @scala.inline
  def apply(): AsymmetricKeyDetails = {
    val __obj = js.Dynamic.literal()
    __obj.asInstanceOf[AsymmetricKeyDetails]
  }
  
  @scala.inline
  implicit class AsymmetricKeyDetailsMutableBuilder[Self <: AsymmetricKeyDetails] (val x: Self) extends AnyVal {
    
    @scala.inline
    def setDivisorLength(value: Double): Self = StObject.set(x, "divisorLength", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setDivisorLengthUndefined: Self = StObject.set(x, "divisorLength", js.undefined)
    
    @scala.inline
    def setHashAlgorithm(value: String): Self = StObject.set(x, "hashAlgorithm", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setHashAlgorithmUndefined: Self = StObject.set(x, "hashAlgorithm", js.undefined)
    
    @scala.inline
    def setMgf1HashAlgorithm(value: String): Self = StObject.set(x, "mgf1HashAlgorithm", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setMgf1HashAlgorithmUndefined: Self = StObject.set(x, "mgf1HashAlgorithm", js.undefined)
    
    @scala.inline
    def setModulusLength(value: Double): Self = StObject.set(x, "modulusLength", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setModulusLengthUndefined: Self = StObject.set(x, "modulusLength", js.undefined)
    
    @scala.inline
    def setNamedCurve(value: String): Self = StObject.set(x, "namedCurve", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setNamedCurveUndefined: Self = StObject.set(x, "namedCurve", js.undefined)
    
    @scala.inline
    def setPublicExponent(value: js.BigInt): Self = StObject.set(x, "publicExponent", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setPublicExponentUndefined: Self = StObject.set(x, "publicExponent", js.undefined)
    
    @scala.inline
    def setSaltLength(value: Double): Self = StObject.set(x, "saltLength", value.asInstanceOf[js.Any])
    
    @scala.inline
    def setSaltLengthUndefined: Self = StObject.set(x, "saltLength", js.undefined)
  }
}
