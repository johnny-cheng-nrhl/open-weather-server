package org.hautelook.http4squickstart

import cats._
import cats.data.ValidatedNel
import cats.effect._
import cats.implicits._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.util.Try

object ForecastApp extends IOApp {
  case class ForecastRequest(val latitude: Double, val longitude: Double)

  sealed trait ForecastResponse

  case class Hot(temperature: Double, description: String) extends ForecastResponse
  case class Cold(temperature: Double, description: String) extends ForecastResponse
  case class Moderate(temperature: Double, description: String) extends ForecastResponse

  object DoubleVar {
    def unapply(str: String): Option[Double] = {
      if (!str.isEmpty) {
        Try(str.toDouble).toOption
      } else
        None
    }
  }

  implicit val doubleQueryParamDecoder = new QueryParamDecoder[Double] {
    override def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, Double] =
      QueryParamDecoder.doubleQueryParamDecoder.decode(value)
  }

  object latQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lat")
  object lonQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lon")

//  def getTemperatureForecast(request: ForecastRequest): F[ForecastRequest] = F(request)

  def forecastRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "forecast" :? latQueryParamMatcher(lat) +& lonQueryParamMatcher(lon) =>
        Ok(s"The temperature will be: ${ForecastRequest(lat, lon)} ")

    }

  }
  // curl -X GET  "http://localhost:8080/weather/forecast?lat=41.40338&lon=2.1740"
  import scala.concurrent.ExecutionContext.global

  override def run(args: List[String]): IO[ExitCode] = {
    val apis = Router(
      "/weather" -> ForecastApp.forecastRoutes[IO]
    ).orNotFound

    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

  }
}
