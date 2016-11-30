package com.github.joe.adams.steps.normalizedata

import com.github.joe.adams.service.Service.HasExecutor

import scala.concurrent.{ExecutionContext, Future}

object NormalizeData extends (()=>Future[Unit]) with HasExecutor{
  override def apply():Future[Unit] =PopulateData().map(u=>Shutdown())
}