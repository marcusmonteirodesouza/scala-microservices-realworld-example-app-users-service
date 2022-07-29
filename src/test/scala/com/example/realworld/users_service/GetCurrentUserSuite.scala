package com.example.realworld.users_service

import faker.Faker
import org.scalatest.funsuite.AnyFunSuite
import sttp.model.StatusCode

class GetCurrentUserSuite extends AnyFunSuite {
  test("Should get the Current User") {
    val username = Faker.default.userName()
    val email = Faker.default.emailAddress()
    val password = Faker.default.password()

    val registerUserResponse =
      UsersClient.registerUser(username, email, password)

    assert(registerUserResponse.code == StatusCode.Created)

    registerUserResponse.body match {
      case Right(registeredUser) =>
        val getCurrentUserResponse =
          UsersClient.getCurrentUser(Some(registeredUser.user.token))

        assert(getCurrentUserResponse.code == StatusCode.Ok)

        getCurrentUserResponse.body match {
          case Right(currentUser) =>
            assert(currentUser.user.username == registeredUser.user.username)
            assert(currentUser.user.email == registeredUser.user.email)
            assert(currentUser.user.token.nonEmpty)
            assert(currentUser.user.bio == registeredUser.user.bio)
            assert(currentUser.user.image == registeredUser.user.image)
        }

      case Left(exception) =>
        fail(exception)
    }
  }

  test("Given no token is sent should return Unauthorized") {
    val getCurrentUserResponse =
      UsersClient.getCurrentUser(None)

    assert(getCurrentUserResponse.code == StatusCode.Unauthorized)
  }

  test("Given invalid token is sent should return Unauthorized") {
    val invalidToken =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

    val getCurrentUserResponse =
      UsersClient.getCurrentUser(Some(invalidToken))

    assert(getCurrentUserResponse.code == StatusCode.Unauthorized)
  }
}
