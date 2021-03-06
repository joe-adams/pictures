package com.github.joe.adams.steps.normalizedata

import akka.actor.Terminated
import com.github.joe.adams.db.DbService
import com.github.joe.adams.service.AkkaService
import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future

private[normalizedata] object Shutdown {

  def apply(): Future[Unit] = Impl()

  object Impl extends Shutdown
}

private[normalizedata] trait Shutdown extends (() => Future[Unit]) with HasExecutor {

  override def apply(): Future[Unit] = Future.sequence(Seq(dbshutdown(), shutdownAkka())).map(s => Unit)

  def dbshutdown(): Future[Unit] = DbService().shutdown()

  def shutdownAkka(): Future[Terminated] = AkkaService.shutdownAkka()

}
