package Utils


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
      else
        {
          List[File]()
        }

    }

  def createFile(path:String, prefix: String): File={
    val filename=path+prefix
    var dir = new File(filename)
    dir.createNewFile()
    dir
  }



}