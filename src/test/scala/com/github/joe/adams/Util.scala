package com.github.joe.adams

import com.github.joe.adams.db.{DbService, Jpegs}
import com.github.joe.adams.db.Jpegs

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Util {

  def syncIO[I,O](f:(I=>Future[O]),i:I):O= Await.result(f(i),Duration.Inf)
  def sync[O](f:(()=>Future[O])):O= Await.result(f(),Duration.Inf)

  def truncate()=sync(DbService().truncate)




}
