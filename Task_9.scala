import scala.io.StdIn

object Task_9 extends App {

  print("Enter a number: ")
  val i: Int = StdIn.readInt()
  var str: String = i.toString

  var c: Int = 0
  for (digit <- str ) {
    if (digit == '5') {
      c += 1
    }
  }
  println(f"$i - $c")

}
