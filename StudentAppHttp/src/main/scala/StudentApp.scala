import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContextExecutor
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

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "StudentApp")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  final case class Student(name: String, age: Int, mark: Double)
  final case class StudentJournal(records: List[Student])

  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat3(Student)
  implicit val journalFormat: RootJsonFormat[StudentJournal] = jsonFormat1(StudentJournal)

  // (fake) async database query api
  def getStudent(studentName: String): Future[Option[Student]] = Future {
    val students = read()
    students.find(s => s.name == studentName)
  }

  def getStudents: List[Student] = {
    read()
  }

  def deleteStudent(studentName: String): Future[Done] = {
    val students = read()
    val newList = students.filterNot(s => s.name == studentName)
    write(newList)
    Future { Done }
  }

  def addStudent(journal: StudentJournal): Future[Done] = {
    val students = read()
    val newList = journal.records ::: students
    write(newList)
    Future { Done }
  }

  def write(students: List[Student]): Unit = {
    val fileName: String = "students.txt"
    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    outputStream.writeObject(students)
    outputStream.close()
  }

  def read(): List[Student] = {
    try {
      val inputStream = new ObjectInputStream(new FileInputStream("students.txt"))
      inputStream.readObject.asInstanceOf[List[Student]]
    } catch {
      case e: Exception => List.empty
    }
  }

  def main(args: Array[String]): Unit = {
    val route: Route =
      concat(
        get {
          pathPrefix("student" / Remaining) { name =>
            val maybeItem: Future[Option[Student]] = getStudent(name)
            onSuccess(maybeItem) {
              case Some(student) => complete(student)
              case None => complete("No such student to get\n")
            }
          }
        },
        delete {
          pathPrefix("student" / Remaining) { name =>
            val deleted: Future[Done] = deleteStudent(name)
            onSuccess(deleted) { _ => complete("Student deleted\n")
            }
          }
        },
        post {
          path("student") {
            entity(as[StudentJournal]) { student =>
              val saved: Future[Done] = addStudent(student)
              onSuccess(saved) { _ => complete("Student added\n")
              }
            }
          }
        },
        get {
          path("students") {
            complete(getStudents)
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