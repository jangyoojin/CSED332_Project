package network
import message.shuffle.{FileRequest, FileResponse, ShuffleGrpc}
import message.utils.Stat
import Utils.FileIO
import com.google.protobuf.ByteString

import java.io.{BufferedWriter, File, FileOutputStream, FileWriter}
import scala.io.Source
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import io.grpc.{ManagedChannelBuilder, Status}
import io.grpc.stub.StreamObserver
import io.grpc.{Server, ServerBuilder, Status}
class FileServer(executionContext: ExecutionContext,port:Int,id:Int,tempDir:String)
{
  val logger:Logger = Logger.getLogger(classOf[FileServer].getName)

  var server :Server= null
  def start():Unit=
  {
  server=ServerBuilder.forPort(port).addService(ShuffleGrpc.bindService(new ShuffleImpl, executionContext)).build.start
  }
  def stop():Unit={

  }

  class ShuffleImpl extends ShuffleGrpc.Shuffle
  {
    override def shuffle(responseObserver: StreamObserver[FileResponse]):StreamObserver[FileRequest]={
    val serviceCompanion= new StreamObserver[FileRequest]{
      var firstChecking=true
      var fos :FileOutputStream=null
      override def onError(t:Throwable) ={
        throw t
      }
      override def onNext(fileRequest :FileRequest)
      ={
        if (firstChecking){
          val file = FileIO.createFile(tempDir,s"shuffle-${fileRequest.workerId}-${fileRequest.partitionId}")
          fos = new FileOutputStream(file)

          firstChecking=false
        }
        fileRequest.data.writeTo(fos)
        fos.flush()
      }

      override def onCompleted(): Unit = {
        fos.close()
        val response=new FileResponse(Stat.SUCCESS)
        responseObserver.onNext(response)
        responseObserver.onCompleted()

      }


    }


    }
  }

}