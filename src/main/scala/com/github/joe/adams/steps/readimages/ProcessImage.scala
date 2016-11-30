package com.github.joe.adams.steps.readimages

import akka.stream.scaladsl.{Sink, Source}
import com.github.joe.adams.db.DbService
import com.github.joe.adams.db.DbService.ExifRaw
import com.github.joe.adams.service.AkkaServiceImpl

import scala.concurrent.Future


private[readimages] object ProcessImage{
  def apply(name:String): Future[Option[Int]] = ProcessImageImpl(name)
}

private[readimages] trait ProcessImage extends (String => Future[Option[Int]]) {

  def getMetaData(name: String): Future[Seq[ExifRaw]]

  def addExifs(newExifs: Seq[ExifRaw]): Future[Option[Int]]

}

private[readimages] object ProcessImageImpl extends ProcessImage with AkkaServiceImpl {
  val dbService:DbService=DbService()
  override def getMetaData(name: String): Future[Seq[ExifRaw]] = GetMetaData(name)

  override def addExifs(newExifs: Seq[ExifRaw]): Future[Option[Int]] = if (newExifs.isEmpty)
    Future.successful(None)
  else
    dbService.addExifsRaw(newExifs)

  override def apply(name: String): Future[Option[Int]] = Source.single(name).mapAsync(1)(getMetaData).mapAsync(100)(addExifs).runWith(Sink.last)

}

