package com.github.joe.adams.db

import com.github.joe.adams.Util._

import org.scalatest.FunSuite

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TestDb extends FunSuite{

  object JpegTestAction extends Jpegs.JpegActions[Function0]{
    val actions=Jpegs.actions
    implicit def toFunction[T](future: Future[T]): Function0[T] =()=>Await.result(future,Duration.Inf)

    override def truncate(): () => Int = actions.truncate()

    override def add(names: Seq[String], found: Boolean): () => Option[Int] = actions.add(names,found)

    override def found(names: Seq[String]): () => Int = actions.found(names)

    override def getNameId() = actions.getNameId()
  }


  test("truncate"){
    truncate()
    assert(JpegTestAction.truncate()()==0)

  }


}
