package network
import message.shuffle.{ShuffleGrpc, FileRequest, FileResponse}
import message.utils.Stat
import com.google.protobuf.ByteString


import java.io.File
import scala.io.Source

import scala.concurrent.{Promise, Await}
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

import java.util.logging.Logger

import io.grpc.{ManagedChannelBuilder, Status}
import io.grpc.stub.StreamObserver

class FileClient(host: String, port: Int, id: Int, tempDir: String) {
  val logger: Logger = Logger.getLogger(classOf[FileClient].getName)

  val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext.build
  val blockingStub = ShuffleGrpc.blockingStub(channel)
  val asyncStub = ShuffleGrpc.stub(channel)

  def shutdown(): Unit = {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }

  def shuffle(receiverId: Int): Unit = {
    for {
      file <- FileIO.getList
    }
    {
      val p = Promise[Unit]()
      requestShuffle(file,p);
      Await.ready(p.future,Duration.Inf)
    }
  }

  def requestShuffle(file: File, shufflePromise: Promise[Unit]): Unit = {
    logger.info("[FileClient] Try to send partition")


    val responseObserver = new StreamObserver[FileResponse] ()
    {
      override def onNext(response:FileResponse) :Unit = {
        if (response.status == Stat.SUCCESS){
          shufflePromise.success()
        }

      }

      override def onCompleted(): Unit = {
        logger.info("Done sending")
      }

      override def onError(t: Throwable): Unit = {
        shufflePromise.failure(t)
      }
    }

    val requestObserver = asyncStub.shuffle(responseObserver)

    try {
      val srcLines = Source.fromFile(file).getLines()
      val FileNameArray = file.getName.split('-')
      val shuffleId = FileNameArray(2).toInt

      for ( line<- srcLines)
      {
        val request = FileRequest(workerId=id,partitionId= shuffleId, data=ByteString)
        requestObserver.onNext(request)
      }
    }

   catch{
     case t: Exception =>
       {
         requestObserver.onError(t)
       }
   }
    requestObserver.onCompleted()
  }
}