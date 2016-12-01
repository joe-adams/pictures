package com.github.joe.adams.db

import com.github.joe.adams.db.CrossTableActions.CrossTableActions
import com.github.joe.adams.db.Directory.DirectoryActions
import com.github.joe.adams.db.ExifRaws._
import com.github.joe.adams.db.Exifs.ExifActions
import com.github.joe.adams.db.Jpegs.JpegActions
import com.github.joe.adams.db.TagNames.TagNameActions
import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future

trait DbService {
  def truncate(): Future[Int]

  def exifRaw(filename: String, directory: String, tagtype: Int, tagname: Option[String], description: String): ExifRaw

  def addInitialJpegs(names: Seq[String]): Future[Option[Int]]

  def markFoundImages(): Future[Int]

  def addExifsRaw(newExifs: Seq[ExifRaw]): Future[Option[Int]]

  def populateDirectories(): Future[Option[Int]]

  def populateTagNames(): Future[Int]

  def populateExif(): Future[Int]

  def shutdown(): Future[Unit]

}

private[db] trait DbServiceWithMethods extends DbService with HasExecutor {

  val exifRawActions: ExifRawActions[Future] = ExifRaws.actions
  val jpegActions: JpegActions[Future] = Jpegs.actions
  val tagNamesActions: TagNameActions[Future] = TagNames.actions
  val directoriesActions: DirectoryActions[Future] = Directory.actions
  val exifActions: ExifActions[Future] = Exifs.actions
  val crossTable: CrossTableActions[Future] = CrossTableActions.actions

  override def truncate(): Future[Int] = exifActions.truncate().flatMap((a: Int) => tagNamesActions.truncate())
    .flatMap((b: Int) => jpegActions.truncate()).flatMap((c: Int) => directoriesActions.truncate()).flatMap((c: Int) => exifRawActions.truncate())

  override def exifRaw(filename: String, directory: String, tagtype: Int, tagname: Option[String], description: String): ExifRaw =
    ExifRaw(filename = filename, directory = directory, tagtype = tagtype, tagname = tagname, description = description)

  override def addInitialJpegs(names: Seq[String]): Future[Option[Int]] = jpegActions.add(names, false)

  override def markFoundImages(): Future[Int] = crossTable.markFoundImages()

  override def addExifsRaw(newExifs: Seq[ExifRaw]): Future[Option[Int]] = exifRawActions.add(newExifs)

  override def populateDirectories(): Future[Option[Int]] = crossTable.addDirectories()

  override def populateTagNames(): Future[Int] = tagNamesActions.populateTagNames()

  override def populateExif(): Future[Int] = exifActions.populateExif()

  override def shutdown(): Future[Unit] = ExifRaws.database.shutdown

}

object DbServiceImpl extends DbServiceWithMethods

object DbService {

  type ExifRaw = ExifRaws.ExifRaw
  type Jpeg = Jpegs.Jpeg

  def apply(): DbService = DbServiceImpl
}
