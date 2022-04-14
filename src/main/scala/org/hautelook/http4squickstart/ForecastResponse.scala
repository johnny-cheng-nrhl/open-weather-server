package org.hautelook.http4squickstart

import cats._
import cats.effect._
import cats.implicits._
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe._

case class ForecastResponse(current: CurrentResponse, alerts: List[Alert])
case class CurrentResponse(temp: String, feels_like: String, humidity: Int, weathers: List[Weather])
case class Weather(main: String, description: String)
case class Alert(event: String, description: String)

case class Error(msg: String) extends RuntimeException(msg)

object ForecastResponse {
  implicit val errorDecoder: Decoder[Error] = deriveDecoder[Error]
  implicit def errorEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Error] = jsonOf

  implicit val alertDecoder: Decoder[Alert] = deriveDecoder[Alert]
  implicit def alertEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Alert] = jsonOf

  implicit val alertEncoder: Encoder[Alert] = deriveEncoder[Alert]
  implicit def alertEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Alert] =
    jsonEncoderOf

  implicit val weatherDecoder: Decoder[Weather] = deriveDecoder[Weather]
  implicit def weatherEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Weather] = jsonOf

  implicit val weatherEncoder: Encoder[Weather] = deriveEncoder[Weather]
  implicit def weatherEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Weather] =
    jsonEncoderOf

  implicit val currentResponseDecoder: Decoder[CurrentResponse] = deriveDecoder[CurrentResponse]
  implicit def currentResponseEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, CurrentResponse] = jsonOf

  implicit val currentResponseEncoder: Encoder[CurrentResponse] = deriveEncoder[CurrentResponse]
  implicit def currentResponseEntityEncoder[F[_]: Applicative]: EntityEncoder[F, CurrentResponse] =
    jsonEncoderOf

  implicit val forecastResponseDecoder: Decoder[ForecastResponse] = deriveDecoder[ForecastResponse]
  implicit def forecastResponseEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, ForecastResponse] = jsonOf

  implicit val forecastResponseEncoder: Encoder[ForecastResponse] = deriveEncoder[ForecastResponse]
  implicit def forecastResponseEntityEncoder[F[_]: Applicative]: EntityEncoder[F, ForecastResponse] =
    jsonEncoderOf
}

sealed trait TemperatureResponse

case class Hot(temperature: Double, description: String) extends TemperatureResponse
case class Cold(temperature: Double, description: String) extends TemperatureResponse
case class Moderate(temperature: Double, description: String) extends TemperatureResponse