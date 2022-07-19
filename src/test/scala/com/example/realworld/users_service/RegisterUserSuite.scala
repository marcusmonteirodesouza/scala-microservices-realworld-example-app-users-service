package com.example.realworld.users_service

import faker.Faker
import org.scalatest.funsuite.AnyFunSuite
import sttp.client3.HttpError
import sttp.model.StatusCode

class RegisterUserSuite extends AnyFunSuite {
  test("Should register a new user") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val response = UsersClient.registerUser(username, email, password)

    assert(response.code == StatusCode.Created)

    response.body match {
      case Right(user) =>
        assert(user.user.username == username)
        assert(user.user.email == email)
        assert(user.user.token.nonEmpty)
        assert(user.user.bio.isEmpty)
        assert(user.user.image.isEmpty)
      case Left(exception) =>
        fail(exception)
    }
  }

  test("Given username already exists should return an error") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val registerUserResponse =
      UsersClient.registerUser(username, email, password)

    assert(registerUserResponse.code == StatusCode.Created)

    val newEmail = Faker.default.emailAddress()

    val response = UsersClient.registerUser(username, newEmail, password)

    assert(response.code == StatusCode.UnprocessableEntity)

    assert(response.body.isLeft)

    response.body match {
      case Left(HttpError(body, _)) =>
        assert(body.errors.body.head == "User already exists")
      case Left(exception) => fail(exception)
    }
  }

  test("Given email already exists should return an error") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val registerUserResponse =
      UsersClient.registerUser(username, email, password)

    assert(registerUserResponse.code == StatusCode.Created)

    val newUsername = Faker.default.userName()

    val response = UsersClient.registerUser(newUsername, email, password)

    assert(response.code == StatusCode.UnprocessableEntity)

    assert(response.body.isLeft)

    response.body match {
      case Left(HttpError(body, _)) =>
        assert(body.errors.body.head == "User already exists")
      case Left(exception) => fail(exception)
    }
  }

  test("Given invalid email should return an error") {
    val username = Faker.default.userName()
    val email = "invalid"
    val password = Faker.default.password()

    val response = UsersClient.registerUser(username, email, password)

    assert(response.code == StatusCode.UnprocessableEntity)

    assert(response.body.isLeft)

    response.body match {
      case Left(HttpError(body, _)) =>
        assert(body.errors.body.head == "Invalid email")
      case Left(exception) => fail(exception)
    }
  }

  test("Given password is too short should return an error") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = "1234567"

    val response = UsersClient.registerUser(username, email, password)

    assert(response.code == StatusCode.UnprocessableEntity)

    assert(response.body.isLeft)

    response.body match {
      case Left(HttpError(body, _)) =>
        assert(body.errors.body.head == "Password length must be at least 8")
      case Left(exception) => fail(exception)
    }
  }
}
