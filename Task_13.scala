import scala.io.StdIn

object Task_13 extends App {

  print("Enter a number: ")
  val number: Int = StdIn.readInt()

  def Fib_reversed( n:Int ) : Long = {
    if (n <= 1) {
      return n
    }

    var a: Int = 0
    var b: Int = 1
    var c: Int = 1
    var result: Int = 1

    while (c < n) {
      c = a + b
      result = result + 1
      
      a = b
      b = c
    }
    return result
  }

  println(f"Result: ${Fib_reversed(number)}")
}