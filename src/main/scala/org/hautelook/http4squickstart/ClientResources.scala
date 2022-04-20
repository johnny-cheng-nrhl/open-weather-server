package org.hautelook.http4squickstart

import cats.Monad
import cats.data._
import cats.effect._
import cats.implicits._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityCodec._
import org.http4s._
import org.http4s.client.{Client => Http4sClient}
import com.typesafe.scalalogging.LazyLogging
import org.http4s.Status.Successful

object ClientResources extends LazyLogging {
  def forecasts[F[_] : Concurrent](
    client: Http4sClient[F],
    baseUrl: Uri
  ): ForecastService[F] = {
    import ForecastResponse._
    new ForecastService[F] {
      override def getOneCallWeather(request: ForecastRequest): F[Either[Error, ForecastResponse]] = {
        val queryUrl = baseUrl.withQueryParams(Map("lat" -> request.latitude.toString,
          "lon" -> request.longitude.toString))
        logger.info(s"Query Url: ${queryUrl.path} ${queryUrl.query}")

        val result = client.fetch(Request[F](Method.GET, queryUrl)) {
          case Successful(resp) =>
            resp.as[ForecastResponse].map(_.asRight[Error])
          case res =>
            res.as[Error].map(_.asLeft[ForecastResponse])
        }
        result
      }
    }
  }
}
