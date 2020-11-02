import java.io.{FileInputStream, ObjectInputStream}

import scala.collection._

// abstract
// Scala's Stackable Trait Pattern (new functional view on Decorator Pattern)
// traits mixin
object TraitsMixin extends App {

  // Decorator

  trait Price {
    def price: Int
  }
  class Wheel(p: Price) extends Price {
    def price: Int = {
      p.price + 100
    }
  }
  class Engine(p: Price) extends Price {
    def price: Int = {
      p.price + 1000
    }
  }
  class Car(p: Price) extends Price {
    def price: Int = {
      p.price + 2000
    }
  }

  val car = new Car(new Wheel(new Engine(new Price { override def price: Int = 0 })))
  println(car.price)


  // Stackable Trait Pattern
  trait Queue {
    def put(i: Int): Unit
    def get: Int
  }
  trait Doubling extends Queue {
    abstract override def put(i: Int): Unit = super.put(i * 2)
  }
  trait Incrementing extends Queue {
    abstract override def put(i: Int): Unit = super.put(i + 2)
  }
  class BasicQueue extends Queue {
    private val queue: mutable.Queue[Int] = mutable.Queue.empty
    def put(i: Int): Unit = queue.enqueue(i)
    def get: Int = queue.dequeue()
  }

  val doubleQueue = new BasicQueue with Doubling
  val incrementQueue = new BasicQueue with Incrementing
  val idQueue = new BasicQueue with Doubling with Incrementing

  for {
    i <- 1 to 6
  } {
    doubleQueue.put(i)
    incrementQueue.put(i)
    idQueue.put(i)
  }

  for {
    _ <- 1 to 6
  } {
    println("idQueue")
    println(idQueue.get)
//    println("incrementing")
//    println(incrementQueue.get)
//    println("doubling and incrementing")
//    println(idQueue.get)
  }


}
