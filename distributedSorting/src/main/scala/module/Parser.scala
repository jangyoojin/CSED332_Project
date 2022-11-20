package module

import network.WorkerMeta

object Parser {
  def parse(args: Array[String]): WorkerMeta = {

    val masterIPPort = args(0)

    // args(1) 은 "-I" 여야 함
    val inputDirPaths = args.slice(2, args.length - 2)

    // args(-2) 는 "-O" 여야 함
    val outputDirPath = args(args.length - 1)

    val masterIP = masterIPPort.split(":")(0)
    val masterPort = masterIPPort.split(":")(1).toInt

    new WorkerMeta(inputDirPaths, outputDirPath, masterIP, masterPort, "123.456.789")
  }
}