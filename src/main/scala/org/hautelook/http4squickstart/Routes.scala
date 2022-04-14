package org.hautelook.http4squickstart

import cats.Monad
import cats.effect._
import cats.implicits._
import cats.data.ValidatedNel
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._

import scala.util.Try

object Routes {
  def forecast[F[_]: Sync](service: ForecastService[F]): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    object DoubleVar {
      def unapply(str: String): Option[Double] = {
        if (!str.isEmpty) {
          Try(str.toDouble).toOption
        } else
          None
      }
    }

    def toDoubleOption(str: String): Option[Double] = {
      if (!str.isEmpty)
        Try(str.toDouble).toOption
      else
        None
    }

    implicit val doubleQueryParamDecoder = new QueryParamDecoder[Double] {
      override def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, Double] =
        QueryParamDecoder.doubleQueryParamDecoder.decode(value)
    }

    object latQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lat")
    object lonQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lon")

    implicit val ForecastRequestQueryParamDecoder: QueryParamDecoder[ForecastRequest] =
      QueryParamDecoder[String].emap { str =>
        def failure(details: String): Either[ParseFailure, ForecastRequest] =
          Left(ParseFailure(
            sanitized = "Invalid query parameter: ForecastRequest",
            details = s"'${str}' is not properly formttated: ${details}"
          ))
        str.split(',').toList match {
          case part1 :: part2 :: Nil =>
            (toDoubleOption(part1), toDoubleOption(part2)) match {
              case (Some(x), Some(y)) =>
                Right(ForecastRequest(x, y))
              case _ =>
                failure(details="Part of query cannot parse to Double.")
            }
          case _ =>
            failure(details = "Query parameter does not correspond to format: 'x,y'")
        }
      }

    object ForecastRequestQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[ForecastRequest]("latLong")

    //  def getTemperatureForecast(request: ForecastRequest): F[ForecastRequest] = F(request)
    //TODO: add validation
    def validateLatLong(la: Double, lo: Double):Boolean = la <= 90 && la >= -90 && lo <= 180 && lo >= -180

    // Invalid query parameter handling
    // ValidatingQueryParamDecoderMatcher
    HttpRoutes.of[F] {
      case GET -> Root / "forecast" :? latQueryParamMatcher(lat) +& lonQueryParamMatcher(lon) =>
        for {
          entity <- service.getOneCallWeather(ForecastRequest(lat, lon))
        } yield entity match {
          case Left(res) => Monad[F].pure(InternalServerError)
          case Right(value) => Monad[F].pure(Ok(value))
        }

      case GET -> Root / "forecast" :? ForecastRequestQueryParamMatcher(latLongValidated) =>
        latLongValidated.fold(
          parseFailures => BadRequest(s"Unable to parse argument latLong. Details: ${parseFailures}"),
          latLong => for {
            entity <- service.getOneCallWeather(latLong)
          } yield entity match {
            case Left(res) => Monad[F].pure(InternalServerError)
            case Right(value) => Monad[F].pure(Ok(value))
          }
        )
    }
  }
}
