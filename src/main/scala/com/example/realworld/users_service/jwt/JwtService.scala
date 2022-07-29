package com.example.realworld.users_service.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import java.time.Instant
import scala.util.{Failure, Success, Try}

class JwtService(val jwtIssuer: String,
                 jwtSecondsToExpire: Long,
                 jwtSecretKey: String) {
  val algorithm: Algorithm = Algorithm.HMAC256(jwtSecretKey)

  def generateToken(email: String): String = {
    val now = Instant.now()
    val expiresAt = now.plusSeconds(jwtSecondsToExpire)

    JWT
      .create()
      .withIssuer(jwtIssuer)
      .withSubject(email)
      .withIssuedAt(now)
      .withExpiresAt(expiresAt)
      .sign(algorithm)
  }

  def decodeToken(token: String): Either[Throwable, String] = {
    val verifier = JWT.require(algorithm).withIssuer(jwtIssuer).build()
    Try(verifier.verify(token)) match {
      case Success(decodedJWT) =>
        val now = Instant.now()
        if (now.isAfter(decodedJWT.getExpiresAtAsInstant)) {
          return Left(
            new IllegalArgumentException(
              s"Token expired at ${decodedJWT.getExpiresAt}"))
        }
        val email = decodedJWT.getSubject
        Right(email)
      case Failure(exception) => Left(exception)
    }
  }
}
