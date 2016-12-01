package com.github.joe.adams.db


import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future


private[db] object ExifRaws extends DbBase {

  val exifRaws = TableQuery[ExifRaws]
  val actions: ExifRawActions[Future] = Futures(DBIOS, database)

  trait ExifRawActions[R[_]] extends Actions[R] {
    def truncate(): R[Int]

    def add(newExifs: Seq[ExifRaw]): R[Option[Int]]

    def getAll(): R[Seq[ExifRaw]]

    def getUniqueJpegNames(): R[Seq[String]]

    def getUniqueDirectoryNames(): R[Seq[String]]

    def getTagNameData(): R[Seq[(String, Int, Option[String])]]

  }

  case class ExifRaw(val id: Int = 0, val filename: String, val directory: String, val tagtype: Int, val tagname: Option[String], val description: String)

  class ExifRaws(tag: Tag) extends Table[ExifRaw](tag, "exifraw") {
    def * : ProvenShape[ExifRaw] = (id, filename, directory, tagtype, tagname, description) <> (ExifRaw.tupled, ExifRaw.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def filename = column[String]("filename")

    def directory = column[String]("directory")

    def tagtype = column[Int]("tagtype")

    def tagname = column[Option[String]]("tagname")

    def description = column[String]("description")
  }

  case class Futures(dbio: ExifRawActions[DBIO], db: Database) extends ExifRawActions[Future] with AutoFuture {

    override def truncate(): Future[Int] = dbio.truncate()

    override def add(newExifs: Seq[ExifRaw]): Future[Option[Int]] = dbio.add(newExifs)

    override def getAll(): Future[Seq[ExifRaw]] = dbio.getAll()

    override def getUniqueJpegNames(): Future[Seq[String]] = dbio.getUniqueJpegNames()

    override def getUniqueDirectoryNames(): Future[Seq[String]] = dbio.getUniqueDirectoryNames()

    override def getTagNameData(): Future[Seq[(String, Int, Option[String])]] = dbio.getTagNameData()

  }

  object DBIOS extends ExifRawActions[DBIO] with HasExecutor {

    override def truncate(): DBIO[Int] = exifRaws.delete

    override def add(newExifs: Seq[ExifRaw]): DBIO[Option[Int]] = exifRaws ++= newExifs

    override def getAll(): DBIO[Seq[ExifRaw]] = exifRaws.result

    override def getUniqueJpegNames(): DBIO[Seq[String]] = exifRaws.map(_.filename).distinct.result

    override def getUniqueDirectoryNames(): DBIO[Seq[String]] = exifRaws.map(_.directory).distinct.sorted.result

    override def getTagNameData(): DBIO[Seq[(String, Int, Option[String])]] = exifRaws.map(e => (e.directory, e.tagtype, e.tagname)).distinct.result

  }


}
