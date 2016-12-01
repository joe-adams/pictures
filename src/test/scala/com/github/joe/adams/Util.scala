package com.github.joe.adams

import com.github.joe.adams.db.DbService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Util {

  def syncIO[I, O](f: (I => Future[O]), i: I): O = Await.result(f(i), Duration.Inf)

  def truncate() = sync(DbService().truncate)

  def sync[O](f: (() => Future[O])): O = Await.result(f(), Duration.Inf)


}
