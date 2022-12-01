package utils


import java.io.File


object FileIO
{
  def getFile(path : String, prefix : String):List[File] =
    {
      val dir = new File(path);
      val Files=dir.listFiles();
      if(dir.isDirectory&&dir.exists())
        {
          val temps =Files.filter(x=> x.isFile)
          if (prefix==null) {
            temps.toList
          }
          else temps.filter( x=>x.getName.startsWith(prefix)).toList
        }
      else {
          List[File]()
      }
    }

  def createDir(pathName: String): String = {
    val newDir = new File(s"${System.getProperty("java.io.tmpdir")}/${pathName}")
    newDir.mkdir
    assert(newDir.isDirectory)
    newDir.getAbsolutePath
  }

  def deleteDir(pathName: String): Unit = {
    val delDir = new File(pathName)
    assert(delDir.isDirectory)
    for (file <- getFile(pathName, null)) {
      assert(file.isFile)
      file.delete()
    }
    delDir.delete()
    assert(!delDir.exists())
  }

  def createFile(path:String, prefix: String): File={
    val filename=path+prefix
    var dir = new File(filename)
    dir.createNewFile()
    dir
  }

}