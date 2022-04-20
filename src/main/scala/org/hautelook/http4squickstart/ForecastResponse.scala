package org.hautelook.http4squickstart

import cats._
import cats.effect._
import cats.implicits._
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._

case class ForecastResponse(lat: Double, lon: Double, timezone: String, timezone_offset: Int, current: Current, alerts: Option[List[Alert]])
case class Current(temp: Double, feels_like: Double, humidity: Int, weather: List[Weather])
case class Weather(main: String, description: String)
case class Alert(event: String, description: String)

sealed trait Error extends Throwable
case class GenericError(message: String) extends Error
case object ParseException extends Error
case object UnauthorizedError extends Error

object ForecastResponse {
  implicit val errorDecoder: Decoder[Error] = deriveDecoder[Error]
  implicit def errorEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Error] = jsonOf[F, Error]

  implicit val alertDecoder: Decoder[Alert] = deriveDecoder[Alert]
  implicit def alertEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Alert] = jsonOf[F, Alert]

  implicit val alertEncoder: Encoder[Alert] = deriveEncoder[Alert]
  implicit def alertEntityEncoder[F[_]: Concurrent]: EntityEncoder[F, Alert] =
    jsonEncoderOf

  implicit val weatherDecoder: Decoder[Weather] = deriveDecoder[Weather]
  implicit def weatherEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Weather] = jsonOf[F, Weather]

  implicit val weatherEncoder: Encoder[Weather] = deriveEncoder[Weather]
  implicit def weatherEntityEncoder[F[_]: Concurrent]: EntityEncoder[F, Weather] =
    jsonEncoderOf

  implicit val currentResponseDecoder: Decoder[Current] = deriveDecoder[Current]
  implicit def currentResponseEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Current] = jsonOf[F, Current]

  implicit val currentResponseEncoder: Encoder[Current] = deriveEncoder[Current]
  implicit def currentResponseEntityEncoder[F[_]: Concurrent]: EntityEncoder[F, Current] =
    jsonEncoderOf

  implicit val forecastResponseDecoder: Decoder[ForecastResponse] = deriveDecoder[ForecastResponse]
  implicit def forecastResponseEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, ForecastResponse] =
    jsonOf[F, ForecastResponse]

  implicit val forecastResponseEncoder: Encoder[ForecastResponse] = deriveEncoder[ForecastResponse]
  implicit def forecastResponseEntityEncoder[F[_]: Concurrent]: EntityEncoder[F, ForecastResponse] =
    jsonEncoderOf
}

sealed trait TemperatureResponse

case class Hot(temperature: Double, description: String) extends TemperatureResponse
case class Cold(temperature: Double, description: String) extends TemperatureResponse
case class Moderate(temperature: Double, description: String) extends TemperatureResponse