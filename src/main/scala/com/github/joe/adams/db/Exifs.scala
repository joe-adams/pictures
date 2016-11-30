package com.github.joe.adams.db

import com.github.joe.adams.db.ExifRaws.{DBIO, Database, Futures, database}
import com.github.joe.adams.db.TagNames.DBIO
import com.github.joe.adams.service.Service.HasExecutor
import slick.dbio.Effect.Write
import slick.sql.FixedSqlAction

import scala.concurrent.Future


private[db] object Exifs extends DbBase{

  val exifs = TableQuery[Exifs]


  case class Exif( val id: Int,
                   val filename: Int,
                   val directory: Int,
                   val tagtype: Int,
                   val description: String)

  class Exifs(tag: Tag) extends Table[Exif](tag, "exif") {
    def * : ProvenShape[Exif] = (id, filename, directory, tagtype, description) <> (Exif.tupled, Exif.unapply)

    def id = column[Int]("id", O.PrimaryKey)

    def filename = column[Int]("jpeg_id")

    def directory = column[Int]("directory_id")

    def tagtype = column[Int]("tagtype")

    def description = column[String]("description")
  }

  trait ExifActions[R[_]] extends Actions[R]{
    def truncate(): R[Int]
    def populateExif(): R[Int]
  }

  object DBIOS extends ExifActions[DBIO] with HasExecutor{
    val insertStatement=
      """
        |insert into exif (jpeg_id,directory_id,tagtype,description)
        |select (select j.id from jpeg j where j.filename=e.filename) as jpeg_id,
        |(select d.id from directory as d where d.name=e.directory) as directory_id,
        |tagtype,description from exifraw e
      """.stripMargin

    override def truncate(): DBIO[Int] =exifs.delete

    override def populateExif(): DBIO[Int] =sqlu"#$insertStatement"
  }

  case class Futures(dbio:ExifActions[DBIO],db: Database) extends ExifActions[Future] with AutoFuture{
    override def truncate(): Future[Int] = dbio.truncate()

    override def populateExif(): Future[Int] = dbio.populateExif()
  }

  val actions: ExifActions[Future] =Futures(DBIOS,database)

}
