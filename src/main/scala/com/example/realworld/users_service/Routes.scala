package com.example.realworld.users_service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

import scala.concurrent.Future

final case class User(username: String,
                      email: String,
                      token: String,
                      bio: Option[String],
                      image: Option[String])

final case class RegisterUserRequest(user: RegisterUserRequestUser)
final case class RegisterUserRequestUser(username: String,
                                         email: String,
                                         password: String)

final case class ErrorResponse(errors: ErrorResponseErrors)
final case class ErrorResponseErrors(body: Seq[String])

trait JsonFormats
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with NullOptions {
  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat5(User)

  implicit val registerUserRequestUserFormat
    : RootJsonFormat[RegisterUserRequestUser] = jsonFormat3(
    RegisterUserRequestUser)
  implicit val registerUserRequestFormat: RootJsonFormat[RegisterUserRequest] =
    jsonFormat1(RegisterUserRequest)

  implicit val errorResponseErrorsFormat: RootJsonFormat[ErrorResponseErrors] =
    jsonFormat1(ErrorResponseErrors)
  implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(
    ErrorResponse)
}

class Routes extends Directives with JsonFormats {
  val routes: Route = Route.seal(concat({
    pathPrefix("users") {
      pathEndOrSingleSlash {
        post {
          entity(as[RegisterUserRequest]) { request =>
            onSuccess(registerUser(request)) { user =>
              complete(StatusCodes.Created, user)
            }
          }
        }
      }
    }
  }))

  private def registerUser(request: RegisterUserRequest): Future[User] = {
    Future.successful(
      User(request.user.username, request.user.email, "", None, None))
  }
}
