import scala.io.StdIn

object Task_7 extends App {

  print("Enter first number: ")
  val i: Int = StdIn.readInt()

  print("Enter second number: ")
  val a: Int = StdIn.readInt()

  val div: Double = i.toDouble / a.toDouble
  val mod: Int = i % a

  if (mod == 0) {
    println("True")
    println(div)
  }
  else {
    println("False")
    println(f"Part before point: $div%1.0f")
    val int_part: Int = div.toInt
    val dec_part: Double = div - int_part
    println(f"Part after point $dec_part")
  }
}
