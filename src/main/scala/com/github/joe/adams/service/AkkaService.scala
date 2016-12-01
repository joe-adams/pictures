package com.github.joe.adams.service

import akka.actor.{ActorSystem, Terminated}
import akka.stream.Materializer
import com.typesafe.scalalogging.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}


trait AkkaService {
  implicit val system: ActorSystem

  implicit val executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  val log = Logger(getClass)

}

trait AkkaServiceImpl extends AkkaService {
  override implicit val system: ActorSystem = ServiceConfig.system
  override implicit val executor: ExecutionContextExecutor = ServiceConfig.executor
  override implicit val materializer: Materializer = ServiceConfig.materializer
}

private object Impl extends AkkaServiceImpl

object AkkaService {
  def apply(): AkkaService = Impl

  def shutdownAkka(): Future[Terminated] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val log = Logger(getClass)
    log.debug("Started shutdown of Akka.")
    ServiceConfig.system.terminate().map((t: Terminated) => {
      log.debug(s"Terminated $t")
      t
    })
  }
}