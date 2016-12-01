package com.github.joe.adams.steps.processxml

import com.github.joe.adams.db.DbService
import com.github.joe.adams.readamazon.GetXML
import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future

object ClearDataAndReadXml {
  def apply(): Future[Seq[String]] = ClearDataAndReadXmlImpl()
}

private[processxml] trait ClearDataAndReadXml extends (() => Future[Seq[String]]) {
  def truncateExistingData(): Future[Int]

  def getNames(): Future[Seq[String]]

  def addInitialJpegs(names: Seq[String]): Future[Option[Int]]

}

private[processxml] object ClearDataAndReadXmlImpl extends ClearDataAndReadXml with HasExecutor {
  val dbService: DbService = DbService()

  override def apply(): Future[Seq[String]] = {
    val namesFuture = truncateDataAndGetNames()
    val a = namesFuture
    namesFuture.flatMap(addJpegsAndReturnFutureOfInput)
  }

  def truncateDataAndGetNames(): Future[Seq[String]] = {
    val truncateFuture: Future[Int] = truncateExistingData()
    val namesFuture: Future[Seq[String]] = getNames()
    val tupleFuture = truncateFuture.zip(namesFuture)
    tupleFuture.map(_._2)
  }

  override def truncateExistingData(): Future[Int] = dbService.truncate()

  override def getNames(): Future[Seq[String]] = GetXML()

  def addJpegsAndReturnFutureOfInput(names: Seq[String]): Future[Seq[String]] = addInitialJpegs(names).map(a => names)

  override def addInitialJpegs(names: Seq[String]): Future[Option[Int]] = dbService.addInitialJpegs(names)
}

