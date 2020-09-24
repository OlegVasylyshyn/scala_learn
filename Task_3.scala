import scala.io.StdIn

object Task_3 extends App {
  print("Enter a number: ")
  val i: Int = StdIn.readInt()
  val a: Int = i % 7
  if (a == 0) {
    println(s"Doubled: ${i * 2}")
  }

}
