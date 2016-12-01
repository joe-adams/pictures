package com.github.joe.adams.db

import scala.concurrent.Future


private[db] object TagNames extends DbBase {

  val tagNames = TableQuery[TagNames]
  val actions: TagNameActions[Future] = Futures(DBIOS, database)

  trait TagNameActions[R[_]] extends Actions[R] {
    def truncate(): R[Int]

    def populateTagNames(): R[Int]
  }

  case class TagName(val id: Int, val directory: Int, val tagType: Int, val tagName: Option[String])

  class TagNames(tag: Tag) extends Table[TagName](tag, "tagname") {
    def * : ProvenShape[TagName] = (id, directory, tagType, tagName) <> (TagName.tupled, TagName.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def directory = column[Int]("directory_id")

    def tagType = column[Int]("tagtype")

    def tagName = column[Option[String]]("tagname")
  }

  case class Futures(dbio: TagNameActions[DBIO], db: Database) extends TagNameActions[Future] with AutoFuture {
    override def truncate(): Future[Int] = dbio.truncate()

    override def populateTagNames(): Future[Int] = dbio.populateTagNames()
  }

  object DBIOS extends TagNameActions[DBIO] {
    val insertStatement =
      """insert into tagname (directory_id,tagtype,tagname) select (select d.id from directory as d where d.name=e.directory)
        |as directory_id, tagtype, tagname from exifraw e group by directory,tagtype,tagname""".stripMargin

    sqlu"""insert into tagname (directory_id,tagtype,tagname) select (select d.id from directory as d where d.name=e.directory)
            as directory_id, tagtype, tagname from exifraw e group by directory,tagtype,tagname"""

    override def truncate(): DBIO[Int] = tagNames.delete

    override def populateTagNames(): DBIO[Int] = sqlu"#$insertStatement"
  }


}
