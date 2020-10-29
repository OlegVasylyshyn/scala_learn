
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


// Enum
// Constructor
// this / super
// lazy
// mixed pattern
// type classes

object EnumsConstructorsThisSuperLazyTypeClasses extends App {

  // Enum example
  sealed trait StudentRestError {
    def printError(): Unit
  }
  object StudentRestError {
    case object NotFound extends StudentRestError {
      override def printError(): Unit = ???
    }
    case object ConnectionError extends StudentRestError {
      def printError(): Unit = ???
    }
    val values = List(NotFound, ConnectionError)
  }
  val err: StudentRestError = StudentRestError.NotFound

  err match {
    case StudentRestError.NotFound =>
    case StudentRestError.ConnectionError =>
  }

  //  StudentRestError.values.foreach(err => err.printError())


  // Constructor
  case class Cat(private val name: String) {
    println(s"Was created cat with name: $name")
    Cat.privateField
  }
  val k = Cat("Kopernik")
  val l = Cat("Leo")

  object Cat { // singleton Cat$
    println("Was created companion object for Cat")
    def feedCat(cat: Cat): Unit = println(s"Feeding cat - ${cat.name}")
    private val privateField = "privateFieldA"
  }
  println("Before Cat object")
  Cat.feedCat(k)
  Cat.feedCat(l)


  // this / super
  class A {
    def a = "A"
  }
  class B extends A {
    override def a: String = "B"
    println(s"This a: ${this.a}")
    println(s"Super a: ${super.a}")
  }
  val b: A = new B
  println(b.a)

  // Lazy
  def initString: String = {
    println("initString")
    "str"
  }
  class LazyObject {
    val a: String = initString
    lazy val b: String = initString
  }
  val lo = new LazyObject


  // type classes
  trait MyOrdering[T] {
    def compare(t1: T, t2: T): Int
  }
  case class User(age: Int)
  // anonymous class
  implicit val o = new MyOrdering[User] {
    def compare(
      t1: User,
      t2: User
    ): Int = t1.age - t2.age
  }
  def sort[T: MyOrdering](users: List[T]): List[T] = {
    ???
  }
  sort(List(User(1), User(2)))


}
