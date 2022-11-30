package network

import com.google.protobuf.ByteString
import io.grpc.stub.StreamObserver

import scala.collection.mutable.Map
import scala.concurrent.Promise
import java.util.logging.Logger
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Status}
import message.connection._
import message.utils._
import utils._
import module.Sampler

import java.io.File
import scala.annotation.tailrec
import scala.io.Source

class ClientData (
                   val inputDirPaths: Seq[String],
                   val outputDirPath: String,
                   val masterIP: String,
                   val masterPort: Int,
                   val shuffleServerIP: String,
                   val shuffleServerPort: Int // 30040?
                 ) {}

class NetworkClient(clientData: ClientData) {
  val logger = Logger.getLogger(classOf[NetworkClient].getName)

  val channel:ManagedChannel = ManagedChannelBuilder.forAddress(clientData.masterIP, clientData.masterPort).usePlaintext().build
  val blockingStub: ConnectGrpc.ConnectBlockingStub = ConnectGrpc.blockingStub(channel)
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.stub(channel)

  var workerId: Int = -1 // id
  val workers = Map[Int, WorkerData]()
  var totalWorkerNum: Int = -1

  val sampleLinesNum = 10000 // sample Line 수 결정 필요

  // directory paths
  var sampleDir: String = null;
  var partitionedDir: String = null;
  var shuffledDir: String = null;
  var subpartitionedDir: String = null;

  // shuffleHandling
  var fileServer: FileServer = null
  def requestStart(): Unit = {
    logger.info("requestStart(): sending startRequest to Master")
    val startRequest = new StartRequest(clientData.shuffleServerIP, clientData.shuffleServerPort)
    val startResponse = blockingStub.start(startRequest)
    workerId = startResponse.workerId
    sampleDir = FileIO.createDir("sampleDir")
    logger.info("requestStart(): done!")
  }
  def sample(): Unit = {
    logger.info("sample(): sampling...")
    Sampler.sample(clientData.inputDirPaths.head, sampleDir, sampleLinesNum)
    logger.info("sample(): done!")
  }
  def requestSample(promise: Promise[Unit]): Unit = {
    logger.info("requestSample(): sending sampleRequest with sample to Master...")
    val responseObserver = new StreamObserver[SampleResponse]() {
      override def onNext(sampleResponse: SampleResponse): Unit = {
        if(sampleResponse.status==Stat.SUCCESS) {
          promise.success()
        }
      }
      override def onCompleted(): Unit = {
        logger.info("requestSample(): done!")
      }
      override def onError(throwable: Throwable): Unit = {
        val errorStatus = Status.fromThrowable(throwable)
        logger.warning(s"requestSample(): failed with: ${errorStatus}")
        promise.failure(throwable)
      }
    }

    val requestObserver = asyncStub.sample(responseObserver)
    try {
      val sampleFile = FileIO.getFile(sampleDir, "sample").head
      val sampleSource = Source.fromFile(sampleFile)
      val sampleLines = sampleSource.getLines
      val inputFileNum = {
        val inputFilesLengthSeq = for {
          inputDirPath <- clientData.inputDirPaths
        } yield {
          val inputDir = new File(inputDirPath)
          assert(inputDir.isDirectory)
          inputDir.listFiles.length
        }
        inputFilesLengthSeq.sum
      }
      for (sampleLine <- sampleLines) {
        val request = SampleRequest(workerId = workerId, inputFileNum=inputFileNum, data = ByteString.copyFromUtf8(sampleLine+"\r\n"))
        requestObserver.onNext(request)
      }
      sampleSource.close
    } catch {
      case ex: RuntimeException => {
        requestObserver.onError(ex)
        throw ex
      }
    }

    requestObserver.onCompleted
  }

  @tailrec
  final def requestDivide(): Unit = {
    logger.info("requestDivide(): sending divideRequest to Master...")
    val divideRequest = new DivideRequest(workerId)
    val divideResponse = blockingStub.divide(divideRequest)
    divideResponse.status match {
      case Stat.SUCCESS => {
        totalWorkerNum = divideResponse.workerNum
        var counter = 0
        for (worker <- divideResponse.workers) {
          workers(worker.workerId) = WorkerData.MessageToWorkerData(worker)
          counter = counter + 1
        }
        assert(counter != totalWorkerNum)
      }
      case Stat.FAILURE => {
        logger.warning("requestDivide(): divideReponse's Stat is FAILURE")
        throw new Exception
      }
      case _ => {
        Thread.sleep(5000) // 5초 후 재시도
        requestDivide
      }
    }
  }
  def partition(): Unit = {
    // partitioning
  }

  def startFileServer(): Unit = ???
  def requestShuffle(): Unit = ???
  def shuffle(): Unit = ???
  def requestSort(): Unit = ???
  def stopFileServer(): Unit = ???
  def subPartition(): Unit = ???
  def sort(): Unit = ???
  def shutdown(): Unit = ???
}