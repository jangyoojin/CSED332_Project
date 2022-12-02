package module

import scala.io.Source

import java.io.File
import utils.FileIO

object Divider {
  type Range = (String, String)
  // make a list [ (key) ], sample file에서 key만 받아오기
  def getKeys(sampleDirPath: String): Seq[(String, Int)] = {
    val sampleData = FileIO.getFile(sampleDirPath, "sample-").map(file => Source.fromFile(file))
    var i = 0
    val keys = for {
      key <- sampleData
      line <- key.getLines
      if(!line.isEmpty())
    } yield {
      i = i + 1
      (line.take(10), i)
    }

    sampleData foreach (_.close)
    keys
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
    val pivots = for {
      key <- keys
      if(key._2 % interval == 0)
    } yield key._1

    //pivot으로 range 만들기
    val minString = " " * 10
    val maxString = "~" * 10
    val totalPivots = minString +: pivots :+ maxString
    val ranges = (for {
      twoPivots <- totalPivots.sliding(2).toSeq
    } yield (twoPivots(0), twoPivots(1))).grouped(fileRangeNum)

    (for (range <- ranges) yield {
      ((range.head._1, range.last._2), range)
    }).toSeq
  }
}