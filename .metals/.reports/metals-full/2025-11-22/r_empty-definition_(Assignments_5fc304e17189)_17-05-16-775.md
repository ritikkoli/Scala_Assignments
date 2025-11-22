file://<WORKSPACE>/Week%203%20Projects/Corporate%20Equipment%20Allocation%20Project/equipment-allocation-play-service/app/security/JwtUtil.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -secretKey.
	 -secretKey#
	 -secretKey().
	 -scala/Predef.secretKey.
	 -scala/Predef.secretKey#
	 -scala/Predef.secretKey().
offset: 314
uri: file://<WORKSPACE>/Week%203%20Projects/Corporate%20Equipment%20Allocation%20Project/equipment-allocation-play-service/app/security/JwtUtil.scala
text:
```scala
package security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object JwtUtil {
  private val secretKey = "abc-jwt-key" // Store securely in environment variables
  private val algorithm = Algorithm.HMAC256(secre@@tKey)
  private val issuer = "play-name"

  // Generate a JWT token
  def generateToken(userId: String, expirationMillis: Long = 3600000): String = {
    val now = System.currentTimeMillis()
    JWT.create()
      .withIssuer(issuer)
      .withSubject(userId)
      .withIssuedAt(new Date(now))
      .withExpiresAt(new Date(now + expirationMillis))
      .sign(algorithm)
  }

  // Validate a JWT token
  def validateToken(token: String): Option[String] = {
    try {
      val verifier = JWT.require(algorithm).withIssuer(issuer).build()
      val decodedJWT = verifier.verify(token)
      print(decodedJWT.getSubject)
      Some(decodedJWT.getSubject) // Extract the userId from the token
    } catch {
      case _: JWTVerificationException => None
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 