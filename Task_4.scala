import scala.io.StdIn

object Task_4 extends App {
  print("Enter a number with floating point: ")
  val i: Float = StdIn.readFloat()
  if (i > 0 && i < 1 ) {
    println("The number is in range from 0 to 1")
  }
  else {
    println("The number is not in range from 0 to 1")
  }

}