package com.github.joe.adams.steps.readimages

import java.io.{ByteArrayInputStream, InputStream}

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.drew.imaging.jpeg.JpegMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.Tag
import com.drew.metadata.exif.ExifReader
import com.github.joe.adams.db.DbService
import com.github.joe.adams.db.DbService.{ExifRaw, _}
import com.github.joe.adams.request.HttpGet
import com.github.joe.adams.request.HttpGet.Mapper
import com.github.joe.adams.service.{AkkaServiceImpl, Service}

import scala.collection.JavaConversions._
import scala.concurrent.Future


object GetMetaData{
  private object impl extends GetMetaDataWithMethods with AkkaServiceImpl
  def apply(name:String): Future[Seq[ExifRaw]] = impl(name)
}

private[readimages] trait GetMetaData extends (String => Future[Seq[ExifRaw]]) {
  type NeedsName=(String=>ExifRaw)

  def httpResponseToOutput(name: String, httpResponse: HttpResponse): Future[Seq[ExifRaw]]

  def httpResponseToInputStream(httpResponse: HttpResponse): Future[InputStream]

  def inputStreamToExifRaws(inputStream: InputStream, name: String): Seq[ExifRaw]

  def inputToMetadata(stream: InputStream): Metadata

  def processMetaData(metadata: Metadata): Seq[NeedsName]

  def hasErrors(metadata: Metadata): Boolean

  def getError(metadata: Metadata): String

  def processGoodMetadata(metadata: Metadata): Seq[NeedsName]

  def exifFromTag(tag: com.drew.metadata.Tag): Option[NeedsName]

}

private[readimages] trait GetMetaDataWithMethods extends GetMetaData {
  this:AkkaServiceImpl=>

  val waldoBase=Service.waldo
  def getter[R](url: String, mapper: Mapper[R]): Future[R]=HttpGet[R](url,mapper)
  def url(picName:String)=s"$waldoBase/$picName"
  def exif(filename: String)(directory: String,tagtype: Int,tagname: Option[String],description: String)=
    DbService().exifRaw(filename=filename,directory=directory,tagname=tagname,tagtype=tagtype,description=description)


  override def apply(name: String): Future[Seq[ExifRaw]] = {
    val responseFuture = getter(url(name), httpResponseToOutput(name, _))
    responseFuture recoverWith { case e: Exception => Future.successful(Seq[ExifRaw]()) }
  }

  override def httpResponseToOutput(name: String, httpResponse: HttpResponse): Future[Seq[ExifRaw]] = httpResponseToInputStream(httpResponse).map(inputStreamToExifRaws(_, name))

  override def httpResponseToInputStream(httpResponse: HttpResponse): Future[InputStream] = httpResponse.status match {
    case OK => Unmarshal(httpResponse.entity).to[Array[Byte]].map(new ByteArrayInputStream(_))
    case _ => Future.failed(new Exception("Wrong status type"))
  }

  override def inputStreamToExifRaws(inputStream: InputStream, name: String): Seq[ExifRaw] = {
    val metadata = inputToMetadata(inputStream)
    val processed = processMetaData(metadata)
    processed.map(_ (name))
  }

  override def inputToMetadata(stream: InputStream): Metadata = try
    JpegMetadataReader.readMetadata(stream, Array(new ExifReader()).toSeq)
  finally
    stream.close()

  override def processMetaData(metadata: Metadata): Seq[NeedsName] =
    if (hasErrors(metadata))
      throw new Exception(getError(metadata))
    else
      processGoodMetadata(metadata)

  override def hasErrors(metadata: Metadata): Boolean = metadata.getDirectories.exists(_.getErrors.isEmpty == false)

  override def getError(metadata: Metadata): String = metadata.getDirectories.flatMap(_.getErrors).mkString(", ")

  override def processGoodMetadata(metadata: Metadata): Seq[NeedsName] = metadata.getDirectories.flatMap(_.getTags.flatMap(exifFromTag)).toSeq

  override def exifFromTag(tag: Tag): Option[NeedsName] = {
    if (tag.getDescription == null || tag.getDescription.isEmpty)
      None
    else
      Some(exif(_)(tag.getDirectoryName,tag.getTagType,tagname(tag), tag.getDescription))
  }

  private def tagname(tag:Tag): Option[String] =if(tag.hasTagName) Some(tag.getTagName) else None

}

