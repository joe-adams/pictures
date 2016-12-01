package com.github.joe.adams.db


import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future

private[db] object Jpegs extends DbBase {

  val jpegs = TableQuery[Jpegs]
  val actions = Futures(DBIOS, database)

  trait JpegActions[R[_]] extends Actions[R] {
    def truncate(): R[Int]

    def add(names: Seq[String], found: Boolean): R[Option[Int]]

    def found(names: Seq[String]): R[Int]

    def getNameId(): R[Seq[(String, Int)]]

  }

  case class Jpeg(val id: Int, val filename: String, val found: Boolean)

  class Jpegs(tag: Tag) extends Table[Jpeg](tag, "jpeg") {
    def * : ProvenShape[Jpeg] = (id, filename, found) <> (Jpeg.tupled, Jpeg.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def filename = column[String]("filename")

    def found = column[Boolean]("found")
  }

  case class Futures(dbio: JpegActions[DBIO], db: Database) extends JpegActions[Future] with AutoFuture {
    override def truncate(): Future[Int] = dbio.truncate()

    override def add(names: Seq[String], found: Boolean): Future[Option[Int]] = dbio.add(names, found)

    override def found(names: Seq[String]): Future[Int] = dbio.found(names)

    override def getNameId(): Future[Seq[(String, Int)]] = dbio.getNameId()
  }

  object DBIOS extends JpegActions[DBIO] with HasExecutor {
    override def truncate(): DBIO[Int] = jpegs.delete

    override def add(names: Seq[String], found: Boolean): DBIO[Option[Int]] = jpegs ++= names.map(Jpeg(0, _, found))

    override def found(names: Seq[String]): DBIO[Int] = jpegs.filter(_.filename.inSet(names)).map(_.found).update(true)

    override def getNameId(): DBIO[Seq[(String, Int)]] = jpegs.filter(_.found).map(j => (j.filename, j.id)).result

  }

}
