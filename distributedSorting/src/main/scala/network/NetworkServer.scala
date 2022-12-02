package network

import io.grpc.stub.StreamObserver

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.Map
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import java.io.FileOutputStream
import java.net._
import io.grpc.{Server, ServerBuilder, Status}
import message.connection._
import message.utils.Stat
import utils._
import module.Divider

import scala.util.{Failure, Success}

class NetworkServer(executionContext: ExecutionContext, port:Int, workerNum: Int) { self =>
  require(workerNum > 0, "The number of worker must be positive")

  val logger = Logger.getLogger(classOf[NetworkServer].getName)

  var server: Server = null
  var state: MasterState = MINIT
  var serverDir: String = null

  val workers = Map[Int, WorkerData]()

  /*--------------method for Master lifecycle-------------------*/
  def open(): Unit = {
    server = ServerBuilder.forPort(port)
      .addService(ConnectGrpc.bindService(new ConnectionImpl, executionContext))
      .build
      .start
    logger.info("Server started, listening on " + port)
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
    if (server != null) {
      server.awaitTermination
    }
  }

  /*--------------method for ConnectionImpl method-------------------*/
  class ConnectionImpl() extends ConnectGrpc.Connect {
    override def start(request: StartRequest): Future[StartResponse] = {
      if (state != MINIT) Future.failed(new Exception("fail to connect"))
      else {
        logger.info(s"Start: Worker ${request.ip}:${request.port} send StartRequest")
        workers.synchronized {
          if (workers.size < workerNum) {
            workers(workers.size + 1) = new WorkerData(workers.size + 1, request.ip, request.port)
            if (workers.size == workerNum) {
              state = MSTART
            }
            Future.successful(new StartResponse(workers.size))
          } else {
            Future.failed(new Exception("[start] there are too many workers"))
          }
        }
      }
    }

    override def sample(request: StreamObserver[SampleResponse]): StreamObserver[SampleRequest] = {
      if (state != MINIT && state != MSTART) {
        new StreamObserver[SampleRequest] {
          override def onNext(value: SampleRequest): Unit = {
            new Exception("[sample] Wrong server state")
          }
          override def onError(t: Throwable): Unit = {}
          override def onCompleted(): Unit = {}
        }
      } else {
        logger.info("[sample] Worker send sample")
        new StreamObserver[SampleRequest] {
          var id = -1
          var fileNum = 0
          var file = null
          override def onNext(value: SampleRequest): Unit = {
            id = value.workerId
            fileNum = value.inputFileNum
            //1. make a file to write the sample data in serverDir(: ./master/)
            if (file == null) {
              file = new FileOutputStream(FileIO.createFile(serverDir, s"sample_${value.workerId}"))
            }
            //2. write sample data of value(the request) to sample_00
            value.data.writeTo(file)
            file.flush
          }

          override def onError(t: Throwable): Unit = {
            logger.warning(s"[sample] Worker $id fail to send a sample data ${Status.fromThrowable(t)}")
            throw t
          }

          override def onCompleted(): Unit = {
            logger.info(s"[sample] Worker $id done")
            file.close
            request.onNext(new SampleResponse(status = Stat.SUCCESS))
            request.onCompleted()
            workers.synchronized {
              workers(id).state = WSAMPLE
              workers(id).fileNum = fileNum
            }
            /*------Start dividing: make ranges------------*/
            def checkWorkersState(): Boolean = {
              workers.synchronized {
                if (state == MSTART && workers.size == workerNum && workers.forall{case (_, worker) => worker.state == WSAMPLE}) true
                else false
              }
            }
            if(checkWorkersState()) {
              logger.info(s"[Divide] All workers send sample data so start dividing")
              val future = Future {
                val fileRangeNum = workers.map{case (id, worker) => worker.fileNum}.sum / workerNum
                val ranges = Divider.getRange(serverDir, workerNum, fileRangeNum)
                workers.synchronized {
                  for {
                    (id, worker) <- workers
                  } {
                    worker.mainRange = ranges(id - 1)._1
                    worker.subRange = ranges(id - 1)._2
                  }
                }
              }
              future.onComplete{
                case Success(value) => state = MDIVIDE
                case Failure(exception) => state = FAILED
              }
            }
          }
        }
      }
    }

    override def divide(request: DivideRequest): Future[DivideResponse] = ???

    override def startShuffle(request: StartShuffleRequest): Future[StartShuffleResponse] = ???

    override def sort(request: SortRequest): Future[SortResponse] = ???

    override def terminate(request: TerminateRequest): Future[TerminateResponse] = ???
  }
}