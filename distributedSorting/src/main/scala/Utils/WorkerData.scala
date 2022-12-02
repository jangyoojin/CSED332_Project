package utils

import message.utils.{workerRange,workerInfo}


//start -> sample -> divide -> shuffle -> sort -> terminate
trait MasterState
case object MINIT extends MasterState
case object MSTART extends MasterState
case object MDIVIDE extends MasterState
case object MSHUFFLE extends MasterState
case object MSORT extends MasterState
case object MTERMINATE extends MasterState
case object SUCCESS extends MasterState
case object FAILED extends MasterState

trait WorkerState
case object WINIT extends WorkerState
case object WSTART extends WorkerState
case object WSAMPLE extends WorkerState
case object WDIVIDE extends WorkerState
case object WPARTITION extends WorkerState
case object WSHUFFLE extends WorkerState
case object WSORT extends WorkerState
case object WSUBPARTITION extends WorkerState
case object WFINALSORT extends WorkerState
case object WTERMINATE extends WorkerState

class WorkerData(val workerId:Int, val ip:String, val port:Int)
{
  var state : WorkerState = WINIT
  type Range = (String, String)
  var mainRange : Range  = null
  var subRange : Seq[Range]=null
  var fileNum: Int = 0
}

object WorkerData{
  def workerDataToMessage(worker:WorkerData):workerInfo={
    workerInfo(
      workerId = worker.workerId,
      ip = worker.ip,
      port = worker.port,
      mainRange = Option(workerRange(worker.mainRange._1,worker.mainRange._2)), //option do the implicit conversion
      subRange = for ( x<-worker.subRange)
        yield workerRange (x._1,x._2)
    )
  }

  def MessageToWorkerData (workerMessage : workerInfo):WorkerData={
    val worker = new WorkerData(
      workerMessage.workerId,
      workerMessage.ip,
      workerMessage.port
    )
    worker.mainRange = (workerMessage.mainRange.get.lower,workerMessage.mainRange.get.upper)
    worker.subRange = for (x<-workerMessage.subRange)
      yield {
        (x.lower,x.upper)
      }
    worker.state=WINIT
    worker


  }

}