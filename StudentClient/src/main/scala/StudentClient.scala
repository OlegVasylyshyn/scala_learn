import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}

import scala.concurrent.ExecutionContext
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.io.StdIn.readLine

final case class Student(name: String, age: Int, mark: Double)


object StudentClient extends App{

  implicit val system: ActorSystem = ActorSystem()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val journal = new StudentJournal
  while (true) {
    val choice = journal.menu
    choice match {
      //      case 1 =>
      //        println("Student added")
      //        journal.addStudent()
      case 1 =>
        println("Show students\n")
        journal.showStudents()
      //      case 3 =>
      //        println("Delete a student\n")
      //        journal.deleteStudent()
      case 2 =>
        println("Exit App")
        System.exit(0)
    }
  }
}

class RestClient(implicit ec: ExecutionContext, mat: Materializer, system: ClassicActorSystemProvider) {
  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat3(Student)
  private val baseUrl = "http://localhost:8080/student"

  def fetchStudents: Future[List[Student]] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = baseUrl))
    responseFuture.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[List[Student]]
      case x =>
        System.err.println(s"Unexpected status code ${x.status}")
        Future(Nil)
    }
  }



}

class StudentJournal(implicit ec: ExecutionContext, mat: Materializer, system: ClassicActorSystemProvider) {

  val client = new RestClient
  var cache: Map[String, List[Student]] = Map.empty

  //  def addStudent(): Unit = {
  //    println("Enter Student Name")
  //    val name: String = readLine()
  //    println("Enter Student Age")
  //    val age: Int = readLine().toInt
  //    println("Enter Student average mark")
  //    val mark: Double = readLine().toDouble
  //
  //    val student = Student(name, age, mark)
  //
  //  }

  def showStudents():Unit = {

    if(cache.isEmpty) {
      client.fetchStudents.map(printList)
    } else {
      printList(cache.values.flatten.toList)
    }


    //    print("You can sort the list by:\n" +
    //      "1 - Sort students list by name\n" +
    //      "2 - Sort students list by age\n" +
    //      "3 - Sort student list by average mark\n" +
    //      "4 - Go to main menu\n")
    //    print("chose an action: ")
    //    val action: Int = readLine().toInt
    //    action match {
    //      case 1 => sort("name")
    //      case 2 => sort("age")
    //      case 3 => sort("mark")
    //      case 4 =>
    //      case _ => println("Please chose an action")
    //    }
  }

  //  def sort(mode: String, students: List[Student]): Unit = {
  //
  //    var swapped: Boolean = true
  //    if (mode == "age") {
  //      while (swapped) {
  //        swapped = false
  //        for (i <- 0 until students.length - 1) {
  //          if (students(i).age > students(i + 1).age) {
  //            val temp = students(i)
  //            students(i) = students(i + 1)
  //            students(i + 1) = temp
  //            swapped = true
  //          }
  //        }
  //      }
  //      printList(students)
  //    } else if (mode == "mark") {
  //      while (swapped) {
  //        swapped = false
  //        for (i <- 0 until studList.length - 1) {
  //          if (studList(i).mark > studList(i + 1).mark) {
  //            val temp = studList(i)
  //            studList(i) = studList(i + 1)
  //            studList(i + 1) = temp
  //            swapped = true
  //          }
  //        }
  //      }
  //      printList(studList)
  //    } else if (mode == "name") {
  //      val sorted = studList.sortWith(_.name < _.name)
  //      printList(sorted)
  //    }
  //  }

  //  def deleteStudent(): Unit = {
  //    val studList = loadFile(fileName)
  //    printList(studList)
  //    print("You can delete student:\n" +
  //      "1 - delete by name\n" +
  //      "2 - delete by ordering number\n" +
  //      "3 - Go to main menu\n")
  //    print("Chose an action: ")
  //    val clone = studList.clone()
  //    val action: Int = readLine().toInt
  //    action match {
  //      case 1 =>
  //        print("Type name: ")
  //        val name: String = readLine()
  //        for (std <- clone) {
  //          if (std.name.equals(name)) {
  //            studList.remove(studList.indexOf(std))
  //          }
  //        }
  //      case 2 =>
  //        println("Type ordering number")
  //        val num: Int = readLine().toInt
  //        studList.remove(num)
  //      case 3 =>
  //      case _ =>
  //        println("Please chose an action")
  //    }
  //    printList(studList)
  //  }


  def printList(list: List[Student]): Unit = {
    println("Order num, name, age, av.mark")
    for (student <- list) {
      println(list.indexOf(student) + " " + student.name + " " + student.age + " " + student.mark)
    }
  }

  def menu: Int = {
    var choice = 0
    println("Student Client")
    try {
      //      println("1 - Add a student")
      println("1 - Show students")
      //      println("3 - Delete a student")
      println("4 - Exit the App")
      choice = readLine().toInt
    } catch {
      case e: NumberFormatException => println("Please Enter Valid Input")
    }
    choice
  }
}

// UserRest -> Validation
//   UserService/UserManager -> UserDTO
//     UserDao/UserRepository -> Errors handling
//       fetch from database
// SOLID
