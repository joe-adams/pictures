package com.github.joe.adams.service


import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision}
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContextExecutor

object Service {
  val waldo = ServiceConfig.waldo
  val hostPort = ServiceConfig.hostPort

  trait HasDatabase {
    def database(): Database = ServiceConfig.database
  }

  trait HasExecutor {
    implicit val executor = scala.concurrent.ExecutionContext.Implicits.global
  }

}

private[service] object ServiceConfig {
  val config: Config = ConfigFactory.load()
  val database: Database = Database.forConfig("dbpg")
  val waldo = config.getString("api.waldo")
  val host = config.getString("api.host")
  val port = config.getInt("api.port")
  val hostPort = (host, port)


  implicit val system: ActorSystem = ActorSystem()
  val executor: ExecutionContextExecutor = system.dispatcher
  val materializer: Materializer = {
    val decider: Supervision.Decider = {
      case _: Exception => Supervision.Resume
      case e: Throwable => {
        println(s"System stopped from error! $e")
        e.printStackTrace()
        Supervision.Stop
      }
      case f => {
        println(s"System stopped from unknown cause! $f")
        Supervision.Stop
      }
    }
    val settings = ActorMaterializerSettings(system).withSupervisionStrategy(decider)
    ActorMaterializer(settings)
  }
}



