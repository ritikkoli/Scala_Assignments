object delayedGreeter {

  // Original function (curried)
  def delayedMessage(delayMs: Int)(message: String): Unit = {
    Thread.sleep(delayMs)
    println(message)
  }

  // 1. Partially applied function: always waits 1000 ms
  val oneSecondSay: String => Unit = delayedMessage(1000)

  def main(args: Array[String]): Unit = {

    // 2. Use it multiple times — same timing, different messages
    oneSecondSay("Hello")
    oneSecondSay("This will print after 1 second")
    oneSecondSay("And again after 1 second")
    oneSecondSay("Partial application controls the *behavior*!")
  }
}
