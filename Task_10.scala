import scala.io.StdIn
import scala.collection.mutable.ArrayBuffer

object Task_10 extends App {

  print("Enter a decimal number: ")
  val decimal: Int = StdIn.readInt()
  print("Enter a binary number: ")
  val binary: Int = StdIn.readInt()

  def ToBinary( n:Int ) : String = {
    var binaryNum = ArrayBuffer[Int]()
    var n_mut: Int = n
    var index: Int = 0

    while (n_mut > 0) {
      binaryNum += n_mut % 2
      n_mut = n_mut / 2
      index += 1
    }
    val string = binaryNum.reverse.mkString("")
    return string

  }

  def ToDecimal( n:Int ) : Double = {

    var binary: Int = n
    var dec: Double = 0
    var index: Int = 0

    while (binary != 0) {
      var mod: Int = binary % 10
      dec = dec + mod * scala.math.pow(2, index)
      binary = binary / 10
      index += 1
    }
    return dec
  }

  println(f"Binary: ${ToBinary(decimal)}")
  println(f"Decimal: ${ToDecimal(binary)}")
}