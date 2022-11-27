package module

import scala.io.Source

import java.io.File
import Utils.FileIO

object Divider {
  type Range = (String, String)
  // make a list [ (key) ], sample file에서 key만 받아오기
  def getKeys(sampleDirPath: String): Seq[String] = {
    val sampleData = FileIO.getListofFiles(sampleDirPath, "sample-").map(file => Source.fromFile(file))
    var i = 0
    val keys = for {
      key <- keys
      line <- key.getLines
      if(!line.isEmpty())
    } yield line.take(10)

    keys foreach (key => key.close)
  }

  // make a list [ (workerRangeNum, fileRanges list) ]
  def getRange(sampleDirPath: String, workerRangeNum: Int, fileRangeNum: Int): Seq[(Range,Seq[Range])] = {
    assert(workerRangeNum > 0)
    assert(fileRangeNum > 0)
    assert(sampleDirPath != None)

    val keys = getKeys(sampleDirPath).sorted
    val total_len = keys.length
    val totalRangeNum = workerRangeNum * fileRangeNum

    //totalRangeNum개의 interval이 필요하므로 totalRangeNum-1개의 pivot 추출
    val interval = total_len / (totalRangeNum - 1)
    var i = 0
    val pivots = for {
      key
      <- keys
      i = i + 1
      if (i % interval == 0)
    }
    yield key

    //pivot으로 range 만들기
    val minString = " " * 10
    val maxString = "~" * 10
    val totalPivots = minString +: pivots +: maxString
    val ranges = (for {
      twoPivots <- totalPivots.sliding(2).toSeq
    } yield (twoPivots(0), twoPivots(1))).grouped(fileRangeNum)

    for (range <- ranges) yield {
      ((range.head._1, range.last._2), range)
    }
  }
}