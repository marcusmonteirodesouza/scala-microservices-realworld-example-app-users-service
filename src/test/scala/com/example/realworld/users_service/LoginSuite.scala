package com.example.realworld.users_service

import faker.Faker
import org.scalatest.funsuite.AnyFunSuite
import sttp.model.StatusCode

class LoginSuite extends AnyFunSuite {
  test("Should login an user") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val registerUserResponse =
      UsersClient.registerUser(username, email, password)

    assert(registerUserResponse.code == StatusCode.Created)

    registerUserResponse.body match {
      case Right(registeredUser) =>
        val loginResponse =
          UsersClient.login(registeredUser.user.email, password)

        assert(loginResponse.code == StatusCode.Ok)

        loginResponse.body match {
          case Right(loggedInUser) =>
            assert(loggedInUser.user.username == registeredUser.user.username)
            assert(loggedInUser.user.email == registeredUser.user.email)
            assert(loggedInUser.user.token.nonEmpty)
            assert(loggedInUser.user.bio == registeredUser.user.bio)
            assert(loggedInUser.user.image == registeredUser.user.image)
        }

      case Left(exception) =>
        fail(exception)
    }
  }

  test("Given User is not found should return Unauthorized") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val registerUserResponse =
      UsersClient.registerUser(username, email, password)

    assert(registerUserResponse.code == StatusCode.Created)

    registerUserResponse.body match {
      case Right(registeredUser) =>
        val nonExistingEmail = Faker.default.emailAddress()
        val loginResponse =
          UsersClient.login(nonExistingEmail, password)

        assert(loginResponse.code == StatusCode.Unauthorized)
      case Left(exception) =>
        fail(exception)
    }
  }

  test("Given wrong password should return Unauthorized") {
    val username1 = Faker.default.userName()
    val email1 = Faker.default.emailAddress()
    val password1 = Faker.default.password()

    val registerUser1Response =
      UsersClient.registerUser(username1, email1, password1)

    assert(registerUser1Response.code == StatusCode.Created)

    val username2 = Faker.default.userName()
    val email2 = Faker.default.emailAddress()
    val password2 = Faker.default.password()

    val registerUser2Response =
      UsersClient.registerUser(username2, email2, password2)

    assert(registerUser2Response.code == StatusCode.Created)

    registerUser1Response.body match {
      case Right(registeredUser1) =>
        val loginResponse =
          UsersClient.login(registeredUser1.user.email, password2)

        assert(loginResponse.code == StatusCode.Unauthorized)
      case Left(exception) =>
        fail(exception)
    }
  }
}
