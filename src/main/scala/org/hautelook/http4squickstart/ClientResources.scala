package org.hautelook.http4squickstart

import cats.data._
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.client.{Client => Http4sClient}
import ForecastResponse._
import org.http4s.Status.Successful

object ClientResources {
  // Concurrent instead of sync???
  def forecasts[F[_] : Concurrent](
    client: Http4sClient[F],
    baseUrl: Uri
  ): ForecastService[F] =
    new ForecastService[F] {
      override def getOneCallWeather(request: ForecastRequest): F[Either[Error, ForecastResponse]] = {
        val queryUrl = baseUrl.withQueryParams(Map("lat" -> request.latitude.toString,
          "lon" -> request.longitude.toString))
        client.fetch(Request[F](Method.GET, queryUrl)) {
          case Successful(resp) => resp.as[ForecastResponse].map(_.asRight[Error])
          case res => res.as[Error].map(_.asLeft[ForecastResponse])
        }
      }
    }
}
