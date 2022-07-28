package com.example.realworld.users_service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.example.realworld.users_service.healthcheck.HealthCheckService
import com.example.realworld.users_service.jwt.JwtService
import com.example.realworld.users_service.users.UsersService
import slick.jdbc.JdbcBackend.Database

import java.time.Clock
import scala.compat.java8.DurationConverters.DurationOps
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] =
      ActorSystem(Behaviors.empty, "users-service")
    implicit val executionContext: ExecutionContextExecutor =
      system.executionContext

    val db = Database.forConfig("db")

    val usersService = new UsersService(db)

    implicit val clock: Clock = Clock.systemUTC()

    val jwtService = new JwtService(
      system.settings.config.getString("server.jwt-issuer"),
      system.settings.config.getLong("server.jwt-seconds-to-expire"),
      system.settings.config.getString("server.jwt-secret-key")
    )

    val healthCheckService = new HealthCheckService(db)

    val routes = new Routes(usersService, jwtService, healthCheckService).routes

    val serverBinding = Http()
      .newServerAt(system.settings.config.getString("server.host"),
                   system.settings.config.getInt("server.port"))
      .bind(routes)
      .map(
        _.addToCoordinatedShutdown(
          system.settings.config
            .getDuration("server.hard-termination-deadline-duration-seconds")
            .toScala)
      )

    serverBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/",
                        address.getHostString,
                        address.getPort)
      case Failure(exception) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system",
                         exception)
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
