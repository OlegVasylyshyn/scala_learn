

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.unmarshalling.Unmarshal
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._


object StudentClient {

  final case class Student(name: String, age: Int, mark: Double)

  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat3(Student)

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/student"))

    val result = response.map {
      case HttpResponse(StatusCodes.OK, headers, entity, _) => {
        val students = Unmarshal(entity).to[List[Student]]
        printList(students)
      }
      case x => s"Unexpected status code ${x.status}"

    }

    println(Await.result(result, 10.seconds))

  }
  def printList(list: List[Student]): Unit = {
    println("Order num, name, age, av.mark")
    for (student <- list) {
      println(list.indexOf(student) + " " +student.name + " " + student.age + " " + student.mark)
    }
  }
}


