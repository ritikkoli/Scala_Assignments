package Assignment4


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf

object KafkaUserEventReader {
  def main(args: Array[String]): Unit = {

    // ------------------------------------
    // SparkSession initialization
    // ------------------------------------
    val spark = SparkSession.builder()
      .appName("KafkaUserEventReader")
      .master("local[*]")  // for local testing
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------
    // Kafka configuration
    // ------------------------------------
    val kafkaBootstrapServers = "localhost:9092"
    val topic = "user-events"

    // ------------------------------------
    // Protobuf Descriptor
    // ------------------------------------
    //val descriptorFile = "/Users/vinodh/protos/UserEvent.desc"
    //val messageType = "example.UserEvent"

    val descriptorFile =
      "/Users/racit/Downloads/spark-protobuf-main/UserEvent.desc"

    val messageType = "example.UserEvent"


    // ------------------------------------
    // 1. Read Kafka Stream
    // ------------------------------------
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    // ------------------------------------
    // 2. Extract Protobuf binary & deserialize
    // ------------------------------------
    val eventDF = kafkaDF
      .selectExpr("CAST(value AS BINARY) AS value")
      .select(
        from_protobuf($"value", messageType, descriptorFile).alias("event")
      )
      .select("event.*")     // Flatten Struct: userId, action, value

    // ------------------------------------
    // 3. Count events per action
    // ------------------------------------
    val eventsPerAction = eventDF
      .groupBy($"action")
      .count()

    // ------------------------------------
    // 4. Top 5 users by maximum value
    // ------------------------------------
    val topUsers = eventDF
      .groupBy($"userId")
      .agg(max($"value").as("maxValue"))
      .orderBy(desc("maxValue"))
      .limit(5)

    // ------------------------------------
    // Print outputs using two separate streams
    // ------------------------------------
    val actionQuery = eventsPerAction.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", false)
      .queryName("events_per_action")
      .start()

    val topUserQuery = topUsers.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", false)
      .queryName("top_users")
      .start()

    actionQuery.awaitTermination()
    topUserQuery.awaitTermination()
  }
}

