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

  def healthCheck(): Identity[Response[Either[String, String]]] = {
    val url = s"$baseUrl/healthcheck"

    val request = basicRequest
      .get(uri"$url")

    request.send(backend)
  }

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
}
