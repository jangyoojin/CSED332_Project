package main

import utils.FileIO

import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source

object SortingTest {
  def main(): Unit = {
    var bytes = 0
    var i = 0
    val path = "/home/pink/64/output"
    val fileList = FileIO.getFile("/home/pink/64/output", null)
    for (file <- fileList) {
      bytes += Files.size(Paths.get(path + file.getName)).toInt
      if (sort(file) == false)
        println("sort is not correct in output" + i)
      i = i + 1
    }
    println("Bytes of the sum of output files are" + bytes)


  }

  def sort(file: File): Boolean = {
    var result = true
    val prev = " "
    val srcLines = Source.fromFile(file).getLines()
    for (line <- srcLines) {
      if (line.substring(0, 10) < prev) result = false
    }
    result

  }
}
