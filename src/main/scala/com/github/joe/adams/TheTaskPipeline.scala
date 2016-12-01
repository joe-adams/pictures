package com.github.joe.adams

import com.github.joe.adams.steps.normalizedata.NormalizeData
import com.github.joe.adams.steps.processxml.ClearDataAndReadXml
import com.github.joe.adams.steps.readimages.ProcessImages

import scala.concurrent.{ExecutionContext, Future}

trait TheTaskPipeline extends (() => Future[Unit]) {
  def clearDataAndReadXML(): Future[Seq[String]]

  def processImages(imagesNames: Seq[String]): Future[Option[Int]]

  def normalizeData(): Future[Unit]

}

trait TheTaskPipelineWithMethods extends TheTaskPipeline {
  implicit val executor = ExecutionContext.Implicits.global

  override def apply(): Future[Unit] = clearDataAndReadXML().flatMap(processImages).flatMap((a: Option[Int]) => normalizeData())

  override def clearDataAndReadXML(): Future[Seq[String]] = ClearDataAndReadXml()

  override def processImages(imagesNames: Seq[String]): Future[Option[Int]] = ProcessImages(imagesNames)

  override def normalizeData(): Future[Unit] = NormalizeData()


}

object Impl extends TheTaskPipelineWithMethods

object TheTaskPipeline {
  def apply(): Future[Unit] = Impl()
}
