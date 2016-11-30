package com.github.joe.adams

import com.github.joe.adams.db.DbService
import com.github.joe.adams.service.AkkaServiceImpl

import scala.util.Try
import scala.concurrent.Future


object WrapUp extends AkkaServiceImpl{

  def whenDone()={
    log.debug("Should be done")
    println("Should be done...")
    system.terminate()
    DbService().shutdown()
    log.debug("closed")
    println("closed...")

  }


}
