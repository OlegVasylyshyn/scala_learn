import scala.collection.mutable.ListBuffer
import scala.io.StdIn

object Task_2 extends App {

  var nums = new ListBuffer[Int]()
  print("Enter first number: ")
  val i: Int = StdIn.readInt()
  nums += i

  print("Enter second number: ")
  val a: Int = StdIn.readInt()
  nums += a

  print("Enter third number: ")
  val b: Int = StdIn.readInt()
  nums += b

  println(s"Minimum value : ${nums.min} ")
  println(s"Maximum value : ${nums.max} ")
}
