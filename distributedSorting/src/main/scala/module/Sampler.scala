package module

import network.NetworkClient

import scala.io.Source
import java.io.{File, FileOutputStream}
import java.util.logging.Logger


object Sampler {
  def sample(inputDirPath: String, sampleDirPath: String, sampleSize: Int): Unit = {
    val logger = Logger.getLogger(classOf[NetworkClient].getName)
    logger.warning(s"inputDirPath is ${inputDirPath}")
    val inputDir = new File(inputDirPath)
    val headFile = inputDir.listFiles.filter(_.isFile).toList.head
    val source = Source.fromFile(headFile)
    val sampleFileName = sampleDirPath + "/sample"
    val writer = new FileOutputStream(new File(sampleFileName))

    for(line <- source.getLines.take(sampleSize)) {
      writer.write((line + "\r\n").getBytes)
    }
    writer.close
  }
}