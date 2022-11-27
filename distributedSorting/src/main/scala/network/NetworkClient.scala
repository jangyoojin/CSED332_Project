package network

class ClientMeta (
                   val inputDirPaths: Seq[String],
                   val outputDirPath: String,
                   val masterIP: String,
                   val masterPort: Int,
                   val shuffleServerIP: String
                 ) {}

class NetworkClient(clientMeta: ClientMeta) {
  def requestStart(): Unit = ???
  def sample(): Unit = ???
  def requestSample(): Unit = ???
  def requestDivide(): Unit = ???
  def partition(): Unit = ???
  def requestShuffle(): Unit = ???
  def shuffle(): Unit = ???
  def requestSort(): Unit = ???
  def subPartition(): Unit = ???
  def sort(): Unit = ???
  def shutdown(): Unit = ???
}