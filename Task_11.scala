import scala.io.StdIn

object Task_11 extends App {

  print("Enter a number: ")
  val number: Int = StdIn.readInt()

  def Factorial( n:Int ) : Long = {
    if (n == 0) {
      return 1
    }
    return n * Factorial(n-1)
  }

    println(f"Result: ${Factorial(number)}")
}