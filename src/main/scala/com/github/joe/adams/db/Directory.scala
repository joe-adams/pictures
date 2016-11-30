package com.github.joe.adams.db

import com.github.joe.adams.db.ExifRaws.{DBIO, Database, Futures, database}
import com.github.joe.adams.service.Service.HasExecutor
import slick.dbio.Effect.Write
import slick.sql.FixedSqlAction

import scala.concurrent.Future


private[db] object Directory extends DbBase{

  val directories = TableQuery[Directories]

  case class Directory(val id: Int=0, val directory: String)

  def directory(dir:String)=Directory(directory=dir)

  class Directories(tag: Tag) extends Table[Directory](tag, "directory") {
    def * : ProvenShape[Directory] = (id, directory) <> (Directory.tupled, Directory.unapply)

    def id = column[Int]("id", O.PrimaryKey,O.AutoInc)

    def directory = column[String]("name")
  }

  trait DirectoryActions[R[_]] extends Actions[R] {
    def truncate(): R[Int]
    def add(names:Seq[String]): R[Option[Int]]
    def getNameId(): R[Seq[(String,Int)]]
  }

  object DBIOS extends DirectoryActions[DBIO] with HasExecutor{
    override def truncate(): DBIO[Int]=directories.delete
    override def add(names:Seq[String]): DBIO[Option[Int]] = directories ++= names.map(directory)
    override def getNameId(): DBIO[Seq[(String,Int)]] = directories.map(d=>(d.directory,d.id)).result

  }

  case class Futures(dbio:DirectoryActions[DBIO],db: Database) extends DirectoryActions[Future] with AutoFuture{
    override def truncate(): Future[Int] = dbio.truncate()
    override def add(names: Seq[String]): Future[Option[Int]] = dbio.add(names)
    override def getNameId(): Future[Seq[(String,Int)]] = dbio.getNameId()
  }

  val actions: DirectoryActions[Future] =Futures(DBIOS,database)

}
