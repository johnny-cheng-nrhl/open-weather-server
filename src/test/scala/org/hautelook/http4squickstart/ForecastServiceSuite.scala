package org.hautelook.http4squickstart

import cats.effect.IO
import cats.effect._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl._
import org.http4s.circe._
import org.http4s._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ForecastServiceSuite
      extends AnyFunSuite
      with Matchers
      with ScalaCheckPropertyChecks
      with ForecastResponseArbitraries
      with Http4sDsl[IO]
      with Http4sClientDsl[IO] {

  // From Offical documentation `https://http4s.org/v0.18/testing/`
  def check[A](actual: IO[Response[IO]],
               expectedStatus: Status,
               expectedBody: Option[A])(
              implicit ev: EntityDecoder[IO, A]
  ): Boolean = {
    val actualResp = actual.unsafeRunSync()
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck  = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)(
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }

  val success: ForecastService[IO] = new ForecastService[IO] {
    override def getOneCallWeather(request: ForecastRequest): IO[Either[Error, ForecastResponse]] =
      IO.pure {
        request match {
          case ForecastRequest(1.0, 1.0) => Right(ForecastResponse(1.0, 1.0, "", 100, Current(100d, 100d, 0, List()), None))
          case _ => Left(GenericError("Something is wrong"))
        }
      }
  }

  def getTestResources(): HttpApp[IO]  = {
    val forecastRoutes = Routes.forecast(success)
    val routes = Router(("/", forecastRoutes)).orNotFound
    routes
  }

  test("Get Forecast") {
    implicit val forecastResponseEncoder: EntityEncoder[IO, ForecastResponse] =
      jsonEncoderOf
    implicit val forecastResponseDecoder: EntityDecoder[IO, ForecastResponse] =
      jsonOf
    val forecastRoute = getTestResources()

    forAll { (request: ForecastRequest) =>
      (for {
        getRequest <- GET(Uri.unsafeFromString("/forecast").withQueryParams(Map("latLong" -> "1.0,1.0")))
        getResponse <- forecastRoute.run(getRequest)
        getForecastResponse <- getResponse.as[ForecastResponse]
      } yield {
        getResponse.status shouldEqual Ok
        getForecastResponse.lat shouldEqual 1.0d
        getForecastResponse.lon shouldEqual 1.0d
      }).unsafeRunSync()

    }
  }
}
