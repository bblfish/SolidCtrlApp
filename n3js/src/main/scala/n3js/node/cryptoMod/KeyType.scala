package n3js.node.cryptoMod

import org.scalablytyped.runtime.StObject
import scala.scalajs.js
import scala.scalajs.js.`|`
import scala.scalajs.js.annotation.{JSGlobalScope, JSGlobal, JSImport, JSName, JSBracketAccess}

/* Rewritten from type alias, can be one of: 
  - n3js.node.nodeStrings.rsa
  - n3js.node.nodeStrings.`rsa-pss`
  - n3js.node.nodeStrings.dsa
  - n3js.node.nodeStrings.ec
  - n3js.node.nodeStrings.ed25519
  - n3js.node.nodeStrings.ed448
  - n3js.node.nodeStrings.x25519
  - n3js.node.nodeStrings.x448
*/
trait KeyType extends StObject
object KeyType {
  
  @scala.inline
  def dsa: n3js.node.nodeStrings.dsa = "dsa".asInstanceOf[n3js.node.nodeStrings.dsa]
  
  @scala.inline
  def ec: n3js.node.nodeStrings.ec = "ec".asInstanceOf[n3js.node.nodeStrings.ec]
  
  @scala.inline
  def ed25519: n3js.node.nodeStrings.ed25519 = "ed25519".asInstanceOf[n3js.node.nodeStrings.ed25519]
  
  @scala.inline
  def ed448: n3js.node.nodeStrings.ed448 = "ed448".asInstanceOf[n3js.node.nodeStrings.ed448]
  
  @scala.inline
  def rsa: n3js.node.nodeStrings.rsa = "rsa".asInstanceOf[n3js.node.nodeStrings.rsa]
  
  @scala.inline
  def `rsa-pss`: n3js.node.nodeStrings.`rsa-pss` = "rsa-pss".asInstanceOf[n3js.node.nodeStrings.`rsa-pss`]
  
  @scala.inline
  def x25519: n3js.node.nodeStrings.x25519 = "x25519".asInstanceOf[n3js.node.nodeStrings.x25519]
  
  @scala.inline
  def x448: n3js.node.nodeStrings.x448 = "x448".asInstanceOf[n3js.node.nodeStrings.x448]
}
