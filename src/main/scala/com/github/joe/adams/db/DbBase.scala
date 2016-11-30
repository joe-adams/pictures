package com.github.joe.adams.db

import com.github.joe.adams.service.Service.{HasDatabase, HasExecutor}

import scala.concurrent.Future

private [db] trait API extends slick.jdbc.PostgresProfile.API{
  type ProvenShape[R] = slick.lifted.ProvenShape[R]
}

private[db] trait DbBase extends HasDatabase with API{

  trait Actions[R[_]]

  trait AutoFuture extends Actions[Future] with HasExecutor{
    def db:Database
    implicit def toFuture[T](query:DBIO[T]): Future[T] = db.run(query)
  }

}




