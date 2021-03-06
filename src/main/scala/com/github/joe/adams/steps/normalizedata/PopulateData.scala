package com.github.joe.adams.steps.normalizedata

import com.github.joe.adams.db.DbService
import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future


private[normalizedata] object PopulateData {

  def apply(): Future[Unit] = Impl()

  private object Impl extends PopulateData

}

private[normalizedata] trait PopulateData extends (() => Future[Unit]) with HasExecutor {
  val dbService = DbService()

  override def apply(): Future[Unit] = Future.sequence(Seq(populate(), markFoundImages())).map(s => Unit)

  def markFoundImages(): Future[Int] = dbService.markFoundImages()

  def populate(): Future[Unit] = populateDirectories().flatMap(f => afterDirectories())

  def populateDirectories(): Future[Option[Int]] = dbService.populateDirectories()

  def afterDirectories(): Future[Unit] = Future.sequence(Seq(populateTagNames(), populateExif())).map((s: Seq[Int]) => Unit)

  def populateTagNames(): Future[Int] = dbService.populateTagNames()

  def populateExif(): Future[Int] = dbService.populateExif()

}
