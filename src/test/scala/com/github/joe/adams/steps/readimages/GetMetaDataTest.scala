package com.github.joe.adams.steps.readimages

import com.github.joe.adams.db.DbService._
import org.scalatest.FunSuite

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class GetMetaDataTest extends FunSuite {

  test("metadata") {
    val dataF = GetMetaData("0003b8d6-d2d8-4436-a398-eab8d696f0f9.68cccdd4-e431-457d-8812-99ab561bf867.jpg")
    val data: Seq[ExifRaw] = Await.result(dataF, Duration.Inf)
    println(data.size)
  }


}
