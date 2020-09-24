import scala.io.StdIn

object Task_8 extends App {

  print("Enter first number: ")
  val i: Int = StdIn.readInt()

  print("Enter second number: ")
  val a: Int = StdIn.readInt()

  val last_digit1: Int = i % 10
  val last_digit2: Int = a % 10

  if (last_digit1 == last_digit2) {
    println(f"$i $a - true")
  }
  else {
    println(f"$i $a - false")
  }
}

