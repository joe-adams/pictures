package com.github.joe.adams.readamazon

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.github.joe.adams.request.HttpGet
import com.github.joe.adams.request.HttpGet.Mapper
import com.github.joe.adams.service.{AkkaService, AkkaServiceImpl, Service}

import scala.concurrent.Future
import scala.xml.{Elem, XML}


object GetXML {
  def apply(): Future[Seq[String]] = GetXMLImpl()
}

private[readamazon] trait GetXML extends (() => Future[Seq[String]]) {

  override def apply(): Future[Seq[String]]

  def httpResponseToPictureNames(httpResponse: HttpResponse): Future[Seq[String]]

  def responseToStringFuture(httpResponse: HttpResponse): Future[String]

  def stringToXml(xmlString: String): Elem

  def xmlToPictureNames(elem: Elem): Seq[String]

}

private[readamazon] trait GetXMLWithMethods extends GetXML {
  this: AkkaService =>

  val url = Service.waldo

  override def apply() = getter(url, httpResponseToPictureNames)

  def getter[R](url: String, mapper: Mapper[R]): Future[R] = HttpGet[R](url, mapper)

  override def httpResponseToPictureNames(httpResponse: HttpResponse) = responseToStringFuture(httpResponse).map(stringToXml).map(xmlToPictureNames)

  override def responseToStringFuture(httpResponse: HttpResponse) = Unmarshal(httpResponse.entity).to[String]

  override def stringToXml(xmlString: String) = XML.loadString(xmlString)

  override def xmlToPictureNames(elem: Elem) = (elem \\ "Contents").map(_ \\ "Key").map(_.text)

}

private[readamazon] object GetXMLImpl extends GetXMLWithMethods with AkkaServiceImpl



