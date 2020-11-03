import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn.readLine

final case class Student(name: String, age: Int, mark: Double)
final case class StudentRecords(records: List[Student])

object StudentClient extends App{

  implicit val system: ActorSystem = ActorSystem()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val journal = new StudentJournal
  while (true) {
    val choice = journal.menu
    choice match {
      case 1 =>
        println("Add a student")
        journal.addStudent()
      case 2 =>
        println("Show students\n")
        journal.showStudents()
      case 3 =>
        println("Delete a student\n")
        journal.deleteStudent()
      case 4 =>
        println("Update a student\n")
        journal.updateStudent()
      case 5 =>
        println("Exit App")
        System.exit(0)
      case _ =>
    }
  }
}

class RestClient(implicit ec: ExecutionContext, mat: Materializer, system: ClassicActorSystemProvider) {
  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat3(Student)
  implicit val journalFormat: RootJsonFormat[StudentRecords] = jsonFormat1(StudentRecords)
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

    def addStudents(student: List[Student]): Unit = {
      val records = StudentRecords(student)
      val responseFuture: Future[HttpResponse] =
        Http(system).singleRequest(
          HttpRequest(
            method = HttpMethods.POST,
            uri = baseUrl,
            entity = HttpEntity(ContentTypes.`application/json`, records.toJson.prettyPrint)
          )
        )

      responseFuture
        .onComplete {
          case Failure(exception) =>
            System.err.println(s"Was error during add new student. Err: ${exception.getMessage}")
          case Success(HttpResponse(StatusCodes.OK, headers, entity, _)) =>
            println(s"Was successfully added new student")
            Unmarshal(entity).to[String].onComplete {
              case Failure(exception) =>
                println(s"Could not deserialize. Err: ${exception.getMessage}")
              case Success(list) =>
                println(s"Was deserialize list: $list")
            }
        }
    }

  def deleteStudents(studentName: String): Future[Option[Student]] = {
    val newUrl = baseUrl + "/" + studentName
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(HttpMethods.DELETE, uri = newUrl))
    responseFuture.flatMap {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        Unmarshal(entity).to[Option[Student]]
      case x =>
        System.err.println(s"Unexpected status code ${x.status}")
        Future(None)
    }
  }

  def updateStudents(student: List[Student]): Unit = {
    val records = StudentRecords(student)
    val responseFuture: Future[HttpResponse] =
      Http(system).singleRequest(
        HttpRequest(
          method = HttpMethods.PUT,
          uri = baseUrl,
          entity = HttpEntity(ContentTypes.`application/json`, records.toJson.prettyPrint)
        )
      )

    responseFuture
      .onComplete {
        case Failure(exception) =>
          System.err.println(s"Was error during student update. Err: ${exception.getMessage}")
        case Success(HttpResponse(StatusCodes.OK, headers, entity, _)) =>
          println(s"Was successfully updated student")
          Unmarshal(entity).to[String].onComplete {
            case Failure(exception) =>
              println(s"Could not deserialize. Err: ${exception.getMessage}")
            case Success(list) =>
              println(s"Was deserialize list: $list")
          }
      }
  }
}

class StudentJournal(implicit ec: ExecutionContext, mat: Materializer, system: ClassicActorSystemProvider) {

  val client = new RestClient
  var cache: Map[String, List[Student]] = Map.empty

    def addStudent(): Unit = {
      println("Enter Student Name")
      val name: String = readLine()
      println("Enter Student Age")
      val age: Int = readLine().toInt
      println("Enter Student average mark")
      val mark: Double = readLine().toDouble
      val student = Student(name, age, mark)
      client.addStudents(List(student))
      cache = Map.empty
    }


  def showStudents():Unit = {
    load()
    val students = cache.values.flatten.toList
    Thread.sleep(1500)
    print("You can sort the list by:\n" +
      "1 - Sort students list by name\n" +
      "2 - Sort students list by age\n" +
      "3 - Sort student list by average mark\n" +
      "4 - Go to main menu\n")
    print("chose an action: ")
    val action: Int = readLine().toInt
    action match {
      case 1 =>
        printList(students.sortWith(_.name < _.name))
      case 2 =>
        printList(students.sortBy(_.age))
      case 3 =>
        printList(students.sortBy(_.mark))
      case 4 =>
      case _ => println("Please chose an action")
    }
 }


  def deleteStudent(): Unit = {
    load()
    Thread.sleep(1500)
    print("Type the name of the student: ")
    val name: String = readLine()
    client.deleteStudents(name).map(student => println(student))
    cache = Map.empty
    load()
  }


  def updateStudent(): Unit = {
    println("Enter Student Name")
    val name: String = readLine()
    println("Enter Student Age")
    val age: Int = readLine().toInt
    println("Enter Student average mark")
    val mark: Double = readLine().toDouble
    val student = Student(name, age, mark)
    client.updateStudents(List(student))
    cache = Map.empty
  }


  def printList(list: List[Student]): Unit = {
    println("Order num, name, age, av.mark")
    for (student <- list) {
      println(list.indexOf(student) + " " + student.name + " " + student.age + " " + student.mark)
    }
  }


  def load() {
    if(cache.isEmpty) {
      client.fetchStudents.map { list =>
        cache = list.groupBy(_.name)
        list
      }.map(printList)
    } else {
      printList(cache.values.flatten.toList)
    }
  }


  def menu: Int = {
    var choice = 0
    println("Student Client")
    try {
      println("1 - Add a student")
      println("2 - Show students")
      println("3 - Delete a student")
      println("4 - Update a student")
      println("5 - Exit the App")
      choice = readLine().toInt
    } catch {
      case e: NumberFormatException => println("Please Enter Valid Input")
    }
    choice
  }
}
