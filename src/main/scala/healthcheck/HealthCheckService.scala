package healthcheck

import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class HealthCheckService(db: Database)(
    implicit val executionContext: ExecutionContext) {
  def healthCheck(): Future[Either[Throwable, HealthCheckResult]] = {
    implicit val getResultHealthCheck
      : AnyRef with GetResult[HealthCheckResult] = GetResult(
      r => HealthCheckResult(r.<<))

    val query = sql"""SELECT version();""".as[HealthCheckResult]
    db.run(query.asTry).map {
      case Success(v) =>
        v.headOption match {
          case Some(healthCheckResult) => Right(healthCheckResult)
          case None                    => Left(new RuntimeException("healthCheck query failed!"))
        }
      case Failure(exception) => Left(exception)
    }
  }
}
