package com.example.realworld.users_service

import sttp.client3.sprayJson._
import sttp.client3.{
  HttpClientSyncBackend,
  Identity,
  Response,
  ResponseException,
  SttpBackend,
  UriContext,
  basicRequest
}

object UsersClient extends JsonFormats {
  val baseUrl = "http://localhost:8080"

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  def registerUser(username: String, email: String, password: String): Identity[
    Response[Either[ResponseException[ErrorResponse, Exception], UserDto]]] = {
    val url = s"$baseUrl/users"

    val body = RegisterUserRequest(
      RegisterUserRequestUser(username, email, password))

    val request = basicRequest
      .post(uri"$url")
      .body(body)
      .response(asJsonEither[ErrorResponse, UserDto])

    request.send(backend)
  }

  def login(email: String, password: String): Identity[
    Response[Either[ResponseException[ErrorResponse, Exception], UserDto]]] = {
    val url = s"$baseUrl/users/login"

    val body = LoginRequest(LoginRequestUser(email, password))

    val request = basicRequest
      .post(uri"$url")
      .body(body)
      .response(asJsonEither[ErrorResponse, UserDto])

    request.send(backend)
  }

  def getCurrentUser(maybeToken: Option[String]): Identity[
    Response[Either[ResponseException[ErrorResponse, Exception], UserDto]]] = {
    val url = s"$baseUrl/user"

    var request =
      basicRequest.get(uri"$url").response(asJsonEither[ErrorResponse, UserDto])

    maybeToken match {
      case Some(token) =>
        request = request.auth.bearer(token)
      case None =>
    }

    request.send(backend)
  }

  def healthCheck(): Identity[Response[Either[String, String]]] = {
    val request = basicRequest
      .get(uri"$baseUrl")

    request.send(backend)
  }
}
