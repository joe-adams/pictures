package com.github.joe.adams.db


import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future


private[db] object CrossTableActions extends DbBase {

  val actions = Futures(DBIOS, database)

  trait CrossTableActions[R[_]] extends Actions[R] {
    def markFoundImages(): R[Int]

    def addDirectories(): R[Option[Int]]

    def populateTagNames(): R[Int]

    def populateExifs(): R[Int]
  }

  case class Futures(dbio: CrossTableActions[DBIO], db: Database) extends CrossTableActions[Future] with AutoFuture {

    override def markFoundImages(): Future[Int] = dbio.markFoundImages()

    override def addDirectories(): Future[Option[Int]] = dbio.addDirectories()

    override def populateTagNames(): Future[Int] = dbio.populateTagNames()

    override def populateExifs(): Future[Int] = dbio.populateExifs()
  }

  object DBIOS extends CrossTableActions[DBIO] with HasExecutor {
    val exifRaws = ExifRaws.DBIOS
    val jpegs = Jpegs.DBIOS
    val directories = Directory.DBIOS
    val tagNames = TagNames.DBIOS
    val exifs = Exifs.DBIOS

    override def markFoundImages(): DBIO[Int] = exifRaws.getUniqueJpegNames().flatMap(jpegs.found)

    override def addDirectories(): DBIO[Option[Int]] = exifRaws.getUniqueDirectoryNames().flatMap(directories.add)

    override def populateTagNames(): DBIO[Int] = tagNames.populateTagNames()

    override def populateExifs(): DBIO[Int] = exifs.populateExif()

  }


}

