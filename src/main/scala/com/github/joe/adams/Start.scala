package com.github.joe.adams


import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.Future


object Start extends App with HasExecutor {

  val completedFuture: Future[Unit] = TheTaskPipeline()


}
