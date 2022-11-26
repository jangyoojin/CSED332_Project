package module

import java.io.{File, FileOutputStream}
import scala.io.Source

object Sampler {
  def sample(inputDirPath: String, sampleDirPath: String, sampleSize: Int): Unit = {
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