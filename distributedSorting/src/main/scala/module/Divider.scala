package module

import scala.io.Source

import java.io.File
import utils.FileUtils

object Divider {
  type Range = (String, String)
  // make a list [ (key, index) ]
  def getKeys(sampleDirPath: String): Map[String, Int] = {
    val sampleData = FileUtils.getListofFiles(sampleDirPath, "sample-").map(file => Source.fromFile(file))
    var i = 0
    val keys = for {
      key <- keys
      line <- key.getLines
      if(!line.isEmpty())
    } yield line.take(10)

    for {
      (key, index) <- keys.toSeq.groupBy(_.take(10))
    }
    keys foreach (key => key.close)
  }

  // make a list [ (workerRangeNum, fileRanges list) ]
  def getRange(sampleDirPath: String, workerRangeNum: Int, fileRangeNum: Int): Seq[(Range,Seq[Range])] = {
    assert(workerRangeNum > 0)
    assert(fileRangeNum > 0)
    assert(sampleDirPath != None)

    val keyAndIndex = getKeys(sampleDirPath)

  }
}