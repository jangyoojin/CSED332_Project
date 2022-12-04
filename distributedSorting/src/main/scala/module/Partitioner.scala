package module

import java.io.{File, FileOutputStream}
import scala.io.Source
import java.util.logging.Logger

import network.NetworkClient


object Partitioner {
  val logger = Logger.getLogger(classOf[NetworkClient].getName)
  def partition(inputDirPaths: Seq[String], partitionDirPath: String, partitioningPoints: Map[Int, (String, String)]): Any = {
    for {
      inputDirPath <- inputDirPaths
      (file, i) <- getFileList(inputDirPath).zipWithIndex
    } {
      partitionIntoRanges(file.getPath, partitionDirPath + "/partition-", "-" + i,partitioningPoints)
    }
  }

  def subPartition(inputDirPath: String, subPartitionDirPath: String, subPartitioningPoints: Seq[(String, String)]): Any = {
    for {
      file <- getFileList(inputDirPath)
    } {
      val indexingSubPartitioningPoints = for ((r, i)<-subPartitioningPoints.zipWithIndex.toMap) yield (i, r)
      partitionIntoRanges(file.getPath, subPartitionDirPath + "/output.", "", indexingSubPartitioningPoints)
    }
  }

  def getFileList(dirPath: String): List[File] = {
    var fileList = List[File]()
    val directory = new File(dirPath)
    if (directory.exists && directory.isDirectory) {
      fileList = directory.listFiles.filter(_.isFile).toList
    }
    fileList
  }

  def partitionIntoRanges(inputFilePath: String, outputFilePath: String, outputFileNameTag: String, partitioningPoints: Map[Int, (String, String)]): Any = {
    logger.info(s"Partitioner.partitionIntoRanges(): partitioningPoints is ${partitioningPoints}")
    val source = Source.fromFile(inputFilePath)
    for{
      (id, (lower, upper)) <- partitioningPoints // 여기서 id는 해당 range의 값을 담당하는 Reciever Worker의 id에 대응된다.
    } {
      val linesToWrite = source.getLines.toList.filter(line => {
        val keyOfLine = line.take(10)
        keyOfLine >=lower && keyOfLine <= upper
      })
      writeLinesOnOutputFile(linesToWrite, outputFilePath + id + outputFileNameTag)
    }

    source.close
  }

  def writeLinesOnOutputFile(linesToWrite: List[String], outputFilePath: String): Any = {
    val outputFile = new File(outputFilePath)
    val writer = new FileOutputStream(outputFile, outputFile.exists)
    for (line <- linesToWrite) {
      writer.write((line+"\r\n").getBytes)
    }
    writer.close
  }
}