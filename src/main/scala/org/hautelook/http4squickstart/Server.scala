package org.hautelook.http4squickstart

import cats._
import cats.effect._
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{HttpApp, HttpRoutes, Uri}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router

import scala.concurrent.ExecutionContext

object Server extends IOApp {

  val baseUri = Uri.uri("https://api.openweathermap.org")
  val baseUriQS = baseUri.withPath("/data/2.5/onecall")

  val oneCallUri: Uri = baseUriQS.withQueryParams(Map(
    "exclude" -> "minutely,hourly,daily",
    "units" -> "imperial",
    "appid" -> "{PROVIDE-OPEN-WEATHER-KEY}"
  ))

  def run(args: List[String]): IO[ExitCode] =
    runR[IO].use(_ => IO.never)

  def runR[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, Unit] =
    for {
      rs <- routes
      _ <- server(Router("/" -> rs).orNotFound)
    } yield ()

  def server[F[_]: ConcurrentEffect: Timer](
    apis: HttpApp[F]
  ): Resource[F, org.http4s.server.Server[F]] =
    BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource

  def routes[F[_]: Sync: ConcurrentEffect: Timer]: Resource[F, HttpRoutes[F]] =
    for {
      client <- BlazeClientBuilder(ExecutionContext.global).resource
    } yield Routes.forecast[F](ClientResources.forecasts(client, oneCallUri))

}
