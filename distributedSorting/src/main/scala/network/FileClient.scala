package network
import message.shuffle.{ShuffleGrpc, FileRequest, FileResponse}
import message.utils.Stat
import Utils.FileIO
import com.google.protobuf.ByteString


import java.io.File
import scala.io.Source

import scala.concurrent.{Promise, Await}
import scala.concurrent.duration

import java.util.logging.Logger

import io.grpc.{ManagedChannelBuilder, Status} //Status is not used(?)
import io.grpc.stub.StreamObserver

class FileClient(host: String, port: Int, id: Int, currentPath: String) {
  val logger: Logger = Logger.getLogger(classOf[FileClient].getName)
  val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext.build
  val blockingStub = ShuffleGrpc.blockingStub(channel)
  val asyncStub = ShuffleGrpc.stub(channel)

  def shutdown(): Unit = {
    //channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
  }

  def shuffleWithAllReceiver(shuffleId: Int): Unit = {
    for {
      file<-FileIO.getFile(currentPath,s"partition-${shuffleId}")
    }
    {
      val p = Promise[Unit]()
      requestShuffle(file,p);
      Await.ready(p.future,duration.Duration.Inf)
    }
  }

  def requestShuffle(file: File, shufflePromise: Promise[Unit]): Unit = {
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
    //from Server to Client

    val requestObserver = asyncStub.shuffle(responseObserver)
    try {
      val srcLines = Source.fromFile(file).getLines()
      val FileNameArray = file.getName.split('-')
      val receiverId = FileNameArray(2).toInt
      for ( line<- srcLines)
      {
        val sendingData=ByteString.copyFromUtf8(line)
        val request = FileRequest(workerId=id,partitionId= receiverId, data=sendingData)
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
  //from client to Server
}