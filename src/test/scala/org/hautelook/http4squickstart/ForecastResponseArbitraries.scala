package org.hautelook.http4squickstart

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary

trait ForecastResponseArbitraries {
  val textLength = 10
  val textGen: Gen[String] = Gen.listOfN(textLength, Gen.alphaChar).map(_.mkString)

  implicit val forecastRequestArb = Arbitrary[ForecastRequest] {
    for {
      latitude <- arbitrary[Double]
      longtitude <- arbitrary[Double]
    } yield ForecastRequest(latitude, longtitude)
  }

  implicit val alertArb = Arbitrary[Alert] {
    for {
      event <- arbitrary[String]
      description <- arbitrary[String]
    } yield Alert(event, description)
  }

  implicit val weatherArb = Arbitrary[Weather] {
    for {
      main <- arbitrary[String]
      description <- arbitrary[String]
    } yield Weather(main, description)
  }

  implicit val weatherListArb = Gen.containerOf[List, Weather](weatherArb.arbitrary)

  implicit val currentObjArb = Arbitrary[Current] {
    for {
      temp <- Gen.posNum[Double]
      feels_like <- Gen.posNum[Double]
      humidity <- Gen.posNum[Int]
      weatherList <- Gen.containerOf[List, Weather](weatherArb.arbitrary)
    } yield Current(temp, feels_like, humidity, weatherList)
  }

  implicit val forecastResponseGen: Arbitrary[ForecastResponse] = Arbitrary[ForecastResponse] {
    for {
      lat <- Gen.posNum[Double]
      lon <- Gen.posNum[Double]
      timezone <- arbitrary[String]
      timezone_offset <- Gen.posNum[Int]
      currentObj <- currentObjArb.arbitrary
      alertList <- Gen.option(Gen.containerOf[List, Alert](alertArb.arbitrary))
    } yield ForecastResponse(lat, lon, timezone = timezone, timezone_offset = timezone_offset, current = currentObj, alerts = alertList)
  }
}
