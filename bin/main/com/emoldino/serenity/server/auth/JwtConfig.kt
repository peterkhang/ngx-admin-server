package com.emoldino.serenity.server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.emoldino.serenity.server.jpa.own.dto.UserDto
import java.util.*

object JwtConfig {

  const val authHeader = "Authorization"
  private const val secret = "xE1x1o1x8qflc1iYtcRd"
  private const val subject = "sso:::authentication"
  private const val issuer = "everytalk.io"
  private const val audience = "everytalk:::web:::mobile:::client"
  const val realm = issuer
  private const val validityInMs = 36_000_000 // 10 hours
  private val algorithm = Algorithm.HMAC256(secret)

  val verifier: JWTVerifier = JWT
    .require(algorithm)
    .withAudience(audience)
    .withIssuer(issuer)
    .build()

  /**
   * Produce a token for this combination of User and Account
   */
  fun makeToken(user: UserDto, jti: String? = null): String = JWT.create()
    .withSubject(subject)
    .withAudience(audience)
    .withIssuer(issuer)
    .withJWTId(if (jti == null) UUID.randomUUID().toString() else jti)
    .withClaim("uid", user.uuid)
    .withClaim("name", user.name)
    .withClaim("level", user.level)
    .withClaim("status", user.status)
    .withExpiresAt(getExpiration())
    .sign(algorithm)

  /**
   * Calculate the expiration Date based on current time + the given validity
   */
  private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}
