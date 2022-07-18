package com.example.realworld.users_service.users

import akka.http.scaladsl.model.DateTime
import com.github.t3hnar.bcrypt._

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}

class UsersService {
  def registerUser(username: String,
                   email: String,
                   password: String): Future[User] = {
    password.bcryptSafeBounded match {
      case Success(passwordHash) =>
        val id = UUID.randomUUID()
        val now = DateTime.now
        val user = User(id, username, email, passwordHash, None, None, now, now)
        Future.successful(user)
      case Failure(exception) => Future.failed(exception)
    }
  }
}
