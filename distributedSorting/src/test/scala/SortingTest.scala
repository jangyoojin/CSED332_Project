import module.Parser
import network.NetworkClient
import network.NetworkServer
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import utils.FileIO
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source



class SortingTest extends AnyFunSuite {

  var bytes=0
  test("sort") {
    var i = 0
    val fileList = FileIO.getFile("/home/pink/64/output", null)
    assert(sort(fileList))
  }

  def sort(fileList: List[File]): Boolean = {
    val path = "/home/pink/64/output/"
    var result = true
    var prev = " " * 10
    for (file <- fileList) {
      bytes += Files.size(Paths.get(path + file.getName)).toInt
      val srcLines = Source.fromFile(file).getLines()
      for (line <- srcLines) {
        if (line.substring(0, 10) < prev) result = false
        prev = line.substring(0, 10)
      }
    }
    println("Bytes of the sum of output files are " + bytes)
    result
  }

  //마스터 열고 마스터에 값 저


}


