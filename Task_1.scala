import scala.io.StdIn

object Task_1 extends App {
  print("Enter a number from 0 to 24: ")
  val i: Int = StdIn.readInt()
  if (i >= 9 && i <= 18) {
    println("I am at work")
  }
  else {
    println("I rest")
  }
}
