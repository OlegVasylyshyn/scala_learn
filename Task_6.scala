import scala.io.StdIn

object Task_6 extends App {

  print("Enter first number: ")
  val i: Int = StdIn.readInt()

  print("Enter second number: ")
  val a: Int = StdIn.readInt()

  val b: Int = i + a
  if (b >= 11 && b <= 19) {
    println(s"Sum : $b")
  }
}