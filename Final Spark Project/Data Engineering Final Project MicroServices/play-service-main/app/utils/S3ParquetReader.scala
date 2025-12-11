package utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.avro.AvroParquetReader
import org.apache.avro.generic.GenericRecord

object S3ParquetReader {

  private def hadoopConf(): Configuration = {
    val conf = new Configuration()
    conf.set("fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
    conf.set("fs.s3a.access.key", "abc")
    conf.set("fs.s3a.secret.key", "abc")
    conf.set("fs.s3a.endpoint", "s3.amazonaws.com")

    // IMPORTANT FIX for INT96
    conf.setBoolean("parquet.avro.readInt96AsFixed", true)
    conf.setBoolean("parquet.reader.int96AsTimestamp", false)

    conf
  }

  /** List all date folders like event_date=2025-12-08 */
  def listDateFolders(basePath: String): Seq[String] = {
    val conf = hadoopConf()
    val fs = new Path(basePath).getFileSystem(conf)

    fs
      .listStatus(new Path(basePath))
      .filter(_.isDirectory)
      .map(_.getPath.getName)
      .filter(_.startsWith("event_date=")) // only keep valid folders
      .toSeq
  }

  /** Read all Parquet files under a folder */
  def readParquetFolder(folderPath: String): Seq[GenericRecord] = {
    val conf = hadoopConf()
    val fs = new Path(folderPath).getFileSystem(conf)

    val parquetFiles =
      fs.listStatus(new Path(folderPath))
        .filter(f => f.getPath.getName.endsWith(".parquet"))
        .map(_.getPath.toString)
    println("INT96 FLAG = " + conf.get("parquet.avro.readInt96AsFixed"))

    parquetFiles.flatMap(readParquet)
  }

  /** Read a single Parquet file */
  def readParquet(path: String): Seq[GenericRecord] = {

    val conf = hadoopConf()

    val input = HadoopInputFile.fromPath(new Path(path), conf)
    val reader = AvroParquetReader.builder[GenericRecord](input)
      .withConf(conf)
      .build()

    var record = reader.read()
    println("ROW SCHEMA = " + record.getSchema.toString)

    var buffer = Seq.empty[GenericRecord]

    while (record != null) {
      buffer :+= record
      record = reader.read()
    }

    buffer
  }

  def decodeInt96(int96Bytes: Array[Byte]): String = {
    if (int96Bytes == null) return null

    val buf = java.nio.ByteBuffer.wrap(int96Bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    val nanos = buf.getLong
    val julianDay = buf.getInt

    val JGREG = 2299161
    val MILLIS_IN_DAY = 86400000L

    val epochMillis =
      (julianDay - JGREG) * MILLIS_IN_DAY + (nanos / 1000000L)

    java.time.Instant.ofEpochMilli(epochMillis).toString
  }

}
