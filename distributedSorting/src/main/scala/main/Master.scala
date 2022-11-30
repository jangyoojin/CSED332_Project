package network

import scala.concurrent.ExecutionContext
import network.NetworkServer

object Master{
  def main(args: Array[String]): Unit = {
    assert(args.length >= 1 && args(0).toInt > 0)
    val workerNum = args(0).toInt
    val port = {
      if (args.length == 2) {
        args(1).toInt
      } else {
        50051
      }
    }
    val server = new NetworkServer(ExecutionContext.global, port, workerNum)

    server.open()
    server.waitUntilShutdown()

  }
}