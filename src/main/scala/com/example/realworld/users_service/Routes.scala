package com.example.realworld.users_service

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.example.realworld.users_service.custom_exceptions.AlreadyExistsException
import com.example.realworld.users_service.jwt.JwtService
import com.example.realworld.users_service.users.UsersService
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

import scala.concurrent.{ExecutionContext, Future}

final case class UserDto(user: UserDtoUser)
final case class UserDtoUser(username: String,
                             email: String,
                             token: String,
                             bio: Option[String],
                             image: Option[String])

object UserDto {
  def fromUserAndToken(user: users.User, token: String): UserDto = {
    UserDto(UserDtoUser(user.username, user.email, token, user.bio, user.image))
  }
}

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
  implicit val userDtoUserJsonFormat: RootJsonFormat[UserDtoUser] = jsonFormat5(
    UserDtoUser)
  implicit val userDtoJsonFormat: RootJsonFormat[UserDto] = jsonFormat1(
    UserDto.apply)

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

class Routes(usersService: UsersService, jwtService: JwtService)(
    implicit val system: ActorSystem[_],
    implicit val executionContext: ExecutionContext)
    extends Directives
    with JsonFormats {
  implicit def customExceptionHandler: ExceptionHandler = {
    ExceptionHandler {
      case AlreadyExistsException(message, _) =>
        complete(
          StatusCodes.UnprocessableEntity,
          ErrorResponse(errors = ErrorResponseErrors(body = Seq(message))))
      case exception: IllegalArgumentException =>
        complete(
          StatusCodes.UnprocessableEntity,
          ErrorResponse(
            errors = ErrorResponseErrors(body = Seq(exception.getMessage))))
      case exception =>
        system.log.error("Unexpected exception", exception)
        complete(
          StatusCodes.InternalServerError,
          ErrorResponse(
            errors = ErrorResponseErrors(body = Seq("Internal server error"))))
    }
  }

  val routes: Route = Route.seal(concat({
    pathPrefix("users") {
      pathEndOrSingleSlash {
        post {
          entity(as[RegisterUserRequest]) { request =>
            onSuccess(registerUser(request)) { response =>
              complete(StatusCodes.Created, response)
            }
          }
        }
      }
    }
  }))

  private def registerUser(request: RegisterUserRequest): Future[UserDto] = {
    system.log.info("Registering user with username {}, email {}...",
                    request.user.username,
                    request.user.email)
    val registerUserFuture = usersService.registerUser(request.user.username,
                                                       request.user.email,
                                                       request.user.password)

    registerUserFuture.map {
      case Right(user) =>
        system.log.info("User {} registered!", user.id)
        val token = jwtService.generateToken(user)
        UserDto.fromUserAndToken(user, token)
      case Left(exception) =>
        throw exception
    }
  }
}
