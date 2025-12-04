class DownloadTask(val fileName: String, val downloadSpeed: Int) extends Thread {

  override def run(): Unit = {
    for (progress <- 10 to 100 by 10) {
      try {
        Thread.sleep(downloadSpeed)
      } catch {
        case e: InterruptedException =>
          println(s"$fileName download interrupted.")
      }
      println(s"$fileName: $progress% downloaded")
    }
    println(s"$fileName download completed!\n")
  }
}

object FileDownloadSimulator {
  def main(args: Array[String]): Unit = {
    // Creating multiple download tasks with varying speeds
    val file1 = new DownloadTask("File_A.mp4", 200)
    val file2 = new DownloadTask("File_B.pdf", 400)
    val file3 = new DownloadTask("File_C.zip", 150)
    val file4 = new DownloadTask("File_D.jpg", 300)

    // Start all downloads concurrently
    file1.start()
    file2.start()
    file3.start()
    file4.start()

    // Optional: wait for all threads to finish
    file1.join()
    file2.join()
    file3.join()
    file4.join()

    println("All downloads completed!")
  }
}
