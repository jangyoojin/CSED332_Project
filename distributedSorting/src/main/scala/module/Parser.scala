package module

import java.io.File
import network.ClientData

import java.net.InetAddress

object Parser {
  def parse(args: Array[String]): ClientData = {

    val masterIPPort = args(0)

    // args(1) 은 "-I" 여야 함
    val inputDirPaths = args.slice(2, args.length - 2)

    // args(-2) 는 "-O" 여야 함
    val outputDirPath = args(args.length - 1)

    val outputDir = new File(outputDirPath)

    if (!outputDir.exists) {
      outputDir.mkdir()
    }

    val masterIP = masterIPPort.split(":")(0)
    val masterPort = masterIPPort.split(":")(1).toInt

    val shuffleServerPort = 30040
    new ClientData(inputDirPaths, outputDirPath, masterIP, masterPort, InetAddress.getLocalHost.getHostAddress, shuffleServerPort)
  }
}