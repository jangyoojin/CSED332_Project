package module

import java.io.{File, FileOutputStream}
import scala.io.Source
import java.util.logging.Logger

import network.NetworkClient
import utils._

object Sorter {
  /*  - inputDirPath는 input file들이 있는 directory의 path를 저장하고 있음
      - input file에 접근하여 sort를 진행하고 새로운 파일을 생성하여 outputDirPath에 해당하는 directory에 저장 */
  val logger = Logger.getLogger(classOf[NetworkClient].getName)
  def sort(inputDirPath: String, outputDirPath: String): Any = {
    logger.info("Sorter.sort(): start to sort")
    // 은하가 구현한 FileIO.getFile 함수 이용 import utils._
    val unsortedFileList = FileIO.getFile(inputDirPath, "")
    for {
      file <- unsortedFileList
    } {

      logger.info(s"Sorter.sort(): sort file: ${file.getName}")
      sortEachFile(file.getPath, outputDirPath + "/"+ file.getName)
    }
  }

  def sortEachFile(inputFilePath: String, outputFilePath: String): Any = {
    // val inputFile = new File(inputFilePath)
    val inputSource = Source.fromFile(inputFilePath)
    val unsortedLineList = inputSource.getLines.toList
    val sortedLineList = unsortedLineList.sortWith((line1, line2) => compareLines(line1, line2))
      //unsortedLineList
    val outputFile = new File(outputFilePath)
    val fos = new FileOutputStream(outputFile, outputFile.exists)
    for (line <- sortedLineList) {
      fos.write((line + "\r\n").getBytes)
    }
    fos.close

    inputSource.close

    // inputFile.delete // 나중에 다시 보기 -> delete 언제 해줄지 정해야 함
  }

  def compareLines(line1: String, line2: String): Boolean = {
    val key1 = line1.substring(0, 10)
    val key2 = line2.substring(0, 10)
    key1 < key2
  }
}