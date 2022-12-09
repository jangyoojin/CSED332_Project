import utils.FileIO
import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source


class SortingTest extends AnyFunSuite {
  test("sort") {
    var i = 0
    val fileList = FileIO.getFile("/home/pink/64/output", null)
//    for (file <- fileList) {
//      bytes += Files.size(Paths.get(path + file.getName)).toInt
//      assert(sort(file) == true)
//      i = i + 1
//    }
    assert(sort(fileList))
  }

//  def sort(file: File): Boolean = {
//    var result = true
//    val prev = " " * 10
//    val srcLines = Source.fromFile(file).getLines()
//    for (line <- srcLines) {
//      if (line.substring(0, 10) < prev) result = false
//    }
//    result
//  }

  def sort(fileList: List[File]): Boolean = {
    var bytes = 0
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

}
