import scala.io.StdIn

object Task_5 extends App {

  print("Enter first number: ")
  val i: Int = StdIn.readInt()

  print("Enter second number: ")
  val a: Int = StdIn.readInt()

  if (i > a) {
    println(s"Difference : ${i - a} ")
  }
  else {
    println(s"Product : ${i * a} ")
  }
}
