package com.example.realworld.users_service

import org.scalatest.funsuite.AnyFunSuite
import sttp.model.StatusCode

class HealthCheckSuite extends AnyFunSuite {
  test("Should return HTTP Status 200") {
    val response = UsersClient.healthCheck()

    assert(response.code == StatusCode.Ok)
  }
}
