package network
import message.shuffle.{FileRequest, FileResponse, ShuffleGrpc}
import message.utils.Stat
import utils.FileIO
import java.io.FileOutputStream
import scala.concurrent.ExecutionContext
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import io.grpc.stub.StreamObserver
import io.grpc.{Server, ServerBuilder}



class FileServer(executionContext: ExecutionContext,port:Int,id:Int,tempDir:String)
{
  val logger:Logger = Logger.getLogger(classOf[FileServer].getName)
  var server :Server= null
  def start():Unit =
  {
    server = ServerBuilder.forPort(port).addService(ShuffleGrpc.bindService(new ShuffleImpl,executionContext)).build.start
  }

  def shutdownServer(): Unit = {
    if (server != null) {
      server.shutdown
      if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
        server.shutdownNow() //fail shutdown while 5 seconds, shutdown now
      }
    }
  }

  def waitUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination
    }
  }

  class ShuffleImpl extends ShuffleGrpc.Shuffle
  {
    override def shuffle(responseObserver: StreamObserver[FileResponse]):StreamObserver[FileRequest]={
    val fileServer= new StreamObserver[FileRequest]{
      var firstChecking=true
      var fos :FileOutputStream=null
      override def onError(t:Throwable) ={
        throw t
      }
      override def onNext(fileRequest :FileRequest)
      ={
        if (firstChecking) {
          //logger.info("First trying to shuffle, so we need to make File..")
          val file = FileIO.createFile(tempDir, s"shuffle-${fileRequest.workerId}-${fileRequest.partitionId}-")
          fos = new FileOutputStream(file)
          firstChecking = false
        }
        fileRequest.data.writeTo(fos)
        fos.flush()
      }

      override def onCompleted(): Unit = {
        logger.info("requestObserver : finished receiving all files from FileClient")
        fos.close()
        val response=new FileResponse(Stat.SUCCESS)
        responseObserver.onNext(response)
        responseObserver.onCompleted()
      }
    }
    fileServer
    }

  }

}