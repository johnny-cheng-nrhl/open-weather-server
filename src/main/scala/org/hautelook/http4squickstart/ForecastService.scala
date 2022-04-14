package org.hautelook.http4squickstart

trait ForecastService[F[_]] { self =>
  def getOneCallWeather(request: ForecastRequest): F[Either[Error, ForecastResponse]]
}
