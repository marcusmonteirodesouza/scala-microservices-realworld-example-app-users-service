package com.example.realworld.users_service.jwt

import com.example.realworld.users_service.users.User
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.time.Clock

class JwtService(jwtIssuer: String,
                 jwtSecondsToExpire: Long,
                 jwtSecretKey: String)(implicit val clock: Clock) {
  val algorithm: JwtAlgorithm = JwtAlgorithm.HS256

  def generateToken(user: User): String = {
    Jwt.encode(JwtClaim()
                 .about(user.email)
                 .by(jwtIssuer)
                 .issuedNow
                 .expiresIn(jwtSecondsToExpire),
               jwtSecretKey,
               algorithm)
  }
}
