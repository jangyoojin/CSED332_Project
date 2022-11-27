package network

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import java.io.{OutputStream, FileOutputStream, File}
import java.net._

import io.grpc.{Server, ServerBuilder, Status}
import io.grpc.stub.StreamObserver

import message.connection.{ConnectGrpc, SampleRequest, SampleResponse, StartRequest, StartResponse}
import utils._

class NetworkServer(executionContext: ExecutionContext, port:Int, workerNum: Int) { self =>
  require(workerNum > 0, "The number of worker must be positive")

  val logger = Logger.getLogger(classOf[NetworkServer].getName)

  var server: Server = null
  var state: MasterState = MINIT

  var serverDir: String = null

  def start(): Unit = {
    server = ServerBuilder.forPort(port)
      .addService(ConnectGrpc.bindService(new ConnectionImpl, executionContext))
      .build
      .start
    logger.info("Server started, listening on" + port)
    println(s"${InetAddress.getLocalHost.getHostAddress}:${port}")
    sys.addShutdownHook {
      self.shutdownServer()
      logger.info("NetworkServer shut down")
    }
    if (serverDir == null) {
      serverDir = FileIO.createDir("master")
    }
  }

  def shutdownServer(): Unit = {
    if (server != null) {
      server.shutdown
      if(!server.awaitTermination(5, TimeUnit.SECONDS)) {
        server.shutdownNow() //fail shutdown while 5 seconds, shutdown now
      }
    }
    if (serverDir != null) FileIO.deleteDir(serverDir)
  }

  def waitUntilShutdown(): Unit = {
    if (server ! = null) {
      server.awaitTermination
    }
  }

  class ConnectionImpl() extends ConnectGrpc.Connect {

  }
}