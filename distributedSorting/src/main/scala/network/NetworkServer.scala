package network

import java.util.logging.Logger

import scala.concurrent.{ExecutionContext, Future, Promise, Await}
import scala.concurrent.ExecutionContext.Implicits.global //Future가 비동기적으로 실행될 영역에 thread pool 배치

import io.grpc.{Server, ServerBuilder, Status}
import io.grpc.stub.StreamObserver

import utils._

class NetworkServer(executionContext: ExecutionContext, port:Int, workerNum: Int) { self =>
  require(workerNum > 0, "The number of worker must be positive")

  val logger = Logger.getLogger(classOf[NetworkServer].getName)
  // logger.setLevel(loggerLevel.level)

  var server: Server = null
  var state: MasterState = MINIT

  def start(): Unit = {

  }
}