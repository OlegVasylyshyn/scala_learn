package docs.http.scaladsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import scala.concurrent.Future

import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import scala.io.StdIn

object StudentApp {

  implicit val system = ActorSystem(Behaviors.empty, "StudentApp")
  implicit val executionContext = system.executionContext

  final case class Student(name: String, age: Int, mark: Double)
  final case class StudentJournal(record: List[Student])

  implicit val studentFormat = jsonFormat3(Student)
  implicit val journalFormat = jsonFormat1(StudentJournal)

  val fileName: String = "students.txt"
  var fileInputStream: FileInputStream = null
  var students: List[Student] = try {
    fileInputStream = new FileInputStream(fileName)
    loadFile(fileName)
  } catch {
    case e :Exception => List.empty
  }

  // (fake) async database query api
  def getStudent(studentName: String): Future[Option[Student]] = Future {
    students.find(s => s.name == studentName)
  }

  def addStudent(student: StudentJournal): Future[Done] = {
    students = student match {
      case StudentJournal(record) =>
        record ::: students
      case _            => students
    }
    saveStudents(students)
    Future { Done }
  }

  def saveStudents(students: List[Student]): Unit = {
    val fileName: String = "students.txt"
    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    outputStream writeObject (students)
  }

  def loadFile(file: String): List[Student] = {
    val inputStream = new ObjectInputStream(new FileInputStream(file))
    inputStream.readObject.asInstanceOf[List[Student]]
  }

  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        get {
          pathPrefix("student" / Remaining) { name =>
            val maybeItem: Future[Option[Student]] = getStudent(name)

            onSuccess(maybeItem) {
              case Some(student) => complete(student)
              case None       => complete(StatusCodes.NotFound)
            }
          }
        },
        post {
          path("add-student") {
            entity(as[StudentJournal]) { student =>
              val saved: Future[Done] = addStudent(student)
              onSuccess(saved) { _ => complete("Student added\n")
              }
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}