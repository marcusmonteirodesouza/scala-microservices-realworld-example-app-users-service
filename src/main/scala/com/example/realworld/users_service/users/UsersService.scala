package com.example.realworld.users_service.users

import com.example.realworld.users_service.custom_exceptions.AlreadyExistsException
import com.github.t3hnar.bcrypt._
import org.postgresql.util.PSQLException
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UsersService(db: Database)(
    implicit val executionContext: ExecutionContext)
    extends Tables {
  final private val validEmail =
    """^([a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+)@([a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*)$""".r

  def registerUser(username: String,
                   email: String,
                   password: String): Future[Either[Throwable, User]] = {
    if (!isValidEmail(email)) {
      return Future.successful(
        Left(new IllegalArgumentException("Invalid email")))
    }

    if (password.length < 8) {
      return Future.successful(
        Left(new IllegalArgumentException("Password length must be at least 8")))
    }

    password.bcryptSafeBounded match {
      case Success(passwordHash) =>
        val id = UUID.randomUUID()
        val now = Timestamp.from(Instant.now())
        val user = User(id, username, email, passwordHash, None, None, now, now)
        val insertAction = users += user
        db.run(insertAction.asTry).map {
          case Success(_) => Right(user)
          case Failure(exception: PSQLException)
              if (exception.getSQLState == "23505") =>
            Left(AlreadyExistsException("User already exists", exception))
          case Failure(exception: PSQLException) => Left(exception)
          case Failure(exception)                => Left(exception)

        }
      case Failure(exception) => Future.successful(Left(exception))
    }
  }

  private def isValidEmail(email: String) = email match {
    case validEmail(_, _) => true
    case _                => false
  }
}

trait Tables {
  final class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey)

    def username = column[String]("username", O.Unique)

    def email = column[String]("email", O.Unique)

    def passwordHash = column[String]("password_hash")

    def bio = column[Option[String]]("bio")

    def image = column[Option[String]]("image")

    def createdAt = column[Timestamp]("created_at")

    def updatedAt = column[Timestamp]("updated_at")

    override def * =
      (id, username, email, passwordHash, bio, image, createdAt, updatedAt) <> (User.tupled, User.unapply)
  }

  protected val users = TableQuery[UsersTable]
}
