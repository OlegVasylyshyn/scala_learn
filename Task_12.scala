import scala.io.StdIn

object Task_12 extends App {

  print("Enter a number: ")
  val number: Int = StdIn.readInt()

  def Fibonacci( n:Int ) : Long = {
    if (n < 0) {
      print("Enter positive value")
      return 0
    }
    else if (n == 0) {
      return 0
    }
    else if (n == 1) {
      return 1
    }
    else {
      return Fibonacci(n-1) + Fibonacci(n-2)
    }
  }

  println(f"Result: ${Fibonacci(number)}")
}