package network

import module.Parser

import scala.concurrent.{Await, Promise, duration}

object Worker {
  def main(args: Array[String]): Unit = {
    val clientData = Parser.parse(args)
    val networkClient = new NetworkClient(clientData)

    networkClient.requestStart
    networkClient.sample

    val samplePromise = Promise[Unit]()
    networkClient.requestSample(samplePromise)
    Await.ready(samplePromise.future, duration.Duration.Inf)

    networkClient.requestDivide
    networkClient.partition
    networkClient.startFileServer
    networkClient.requestShuffle
    networkClient.shuffle
    networkClient.requestSort
    networkClient.stopFileServer
    networkClient.subPartition
    networkClient.sort
    networkClient.shutdown
  }
}