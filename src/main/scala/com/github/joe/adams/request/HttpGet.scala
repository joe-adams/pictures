package com.github.joe.adams.request

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.github.joe.adams.service.{AkkaService, AkkaServiceImpl, Service}

import scala.concurrent.Future
import scala.concurrent.duration._


private[request] trait HttpGet[R] extends ((String, HttpGet.Mapper[R]) => Future[R]) {
  val ipApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any]

  def makeRequest(url: String): Future[HttpResponse]

  def mapResponse(response: Future[HttpResponse], mapper: (HttpResponse => Future[R])): Future[R]

  override def apply(url: String, mapper: (HttpResponse => Future[R])): Future[R]
}

private[request] trait HttpGetWithMethods[R] extends HttpGet[R] {
  this: AkkaService =>
  override lazy val ipApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] = Http().outgoingConnection(Service.hostPort._1, Service.hostPort._2).idleTimeout(45 seconds)

  override def apply(url: String, mapper: (HttpGet.Mapper[R])): Future[R] = mapResponse(makeRequest(url), mapper)

  override def makeRequest(url: String): Future[HttpResponse] = Source.single(RequestBuilding.Get(url)).via(ipApiConnectionFlow).runWith(Sink.head)

  override def mapResponse(response: Future[HttpResponse], mapper: (HttpGet.Mapper[R])): Future[R] = response.flatMap(mapper)

}

private[request] trait HttpGetImpl[R] extends HttpGetWithMethods[R] with AkkaServiceImpl

object HttpGet {
  type Mapper[R] = (HttpResponse => Future[R])

  private def getter[R](): HttpGetImpl[R] = new HttpGetImpl[R] {}

  def apply[R](url: String, mapper: Mapper[R]): Future[R] = getter[R]()(url, mapper)

  //def waldoPic(string: String)= waldo + "/" + string

}






