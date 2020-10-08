import scala.collection.mutable.ArrayBuffer
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import scala.io.StdIn.readLine


object StudentApp extends App {
  val journal = new StudentJournal
  while (true) {
    val choice = journal.menu
    choice match {
      case 1 =>
        println("Student added")
        journal.addStudent()
      case 2 =>
        println("Show students\n")
        journal.showStudents()
      case 3 =>
        println("Delete a student\n")
        journal.deleteStudent()
      case 4 =>
        println("Exit App")
        System.exit(0)
    }
  }
}

class StudentJournal {
  val fileName: String = "students.txt"

  def addStudent(): Unit = {
    println("Enter Student Name")
    val name: String = readLine()
    println("Enter Student Age")
    val age: Int = readLine().toInt
    println("Enter Student average mark")
    val mark: Double = readLine().toDouble

    val student = Student(name, age, mark)

    var fileInputStream: FileInputStream = null
    val students: ArrayBuffer[Student] = try {
      fileInputStream = new FileInputStream(fileName)
      loadFile(fileName)
    } catch {
      case e :Exception => ArrayBuffer.empty
    }

    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    outputStream writeObject (students += student)
  }

  def showStudents() {
    val studList = loadFile(fileName)
    printList(studList)

    print("You can sort the list by:\n" +
      "1 - Sort students list by name\n" +
      "2 - Sort students list by age\n" +
      "3 - Sort student list by average mark\n" +
      "4 - Go to main menu\n")
    print("chose an action: ")
    val action: Int = readLine().toInt
    action match {
      case 1 => sort("name")
      case 2 => sort("age")
      case 3 => sort("mark")
      case 4 =>
      case _ => println("Please chose an action")
    }
  }

  def sort(mode: String): Unit = {
    val studList = loadFile(fileName)

    var swapped: Boolean = true
    if (mode == "age") {
      while (swapped) {
        swapped = false
        for (i <- 0 until studList.length - 1) {
          if (studList(i).age > studList(i + 1).age) {
            val temp = studList(i)
            studList(i) = studList(i + 1)
            studList(i + 1) = temp
            swapped = true
          }
        }
      }
      printList(studList)
    } else if (mode == "mark") {
      while (swapped) {
        swapped = false
        for (i <- 0 until studList.length - 1) {
          if (studList(i).mark > studList(i + 1).mark) {
            val temp = studList(i)
            studList(i) = studList(i + 1)
            studList(i + 1) = temp
            swapped = true
          }
        }
      }
      printList(studList)
    } else if (mode == "name") {
      val sorted = studList.sortWith(_.name < _.name)
      printList(sorted)
    }
  }

  def deleteStudent(): Unit = {
    val studList = loadFile(fileName)
    printList(studList)
    print("You can delete student:\n" +
      "1 - delete by name\n" +
      "2 - delete by ordering number\n" +
      "3 - Go to main menu\n")
    print("Chose an action: ")
    val clone = studList.clone()
    val action: Int = readLine().toInt
    action match {
      case 1 =>
        print("Type name: ")
        val name: String = readLine()
        for (std <- clone) {
          if (std.name.equals(name)) {
            studList.remove(studList.indexOf(std))
          }
        }
      case 2 =>
        println("Type ordering number")
        val num: Int = readLine().toInt
        studList.remove(num)
      case 3 =>
      case _ =>
        println("Please chose an action")
    }
    printList(studList)
    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    outputStream.writeObject(studList)
  }

  def loadFile(file: String): ArrayBuffer[Student] = {
    val inputStream = new ObjectInputStream(new FileInputStream(file))
    inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
  }

  def printList(list: ArrayBuffer[Student]): Unit = {
    println("Order num, name, age, av.mark")
    for (student <- list) {
      println(list.indexOf(student) + " " +student.name + " " + student.age + " " + student.mark)
    }
  }

  def menu: Int = {
    var choice = 0
    println("Student App")
    try {
      println("1 - Add a student")
      println("2 - Show students")
      println("3 - Delete a student")
      println("4 - Exit the App")
      choice = readLine().toInt
    } catch {
      case e: NumberFormatException => println("Please Enter Valid Input")
    }
    choice
  }
}

case class Student(name: String, age: Int, mark: Double) extends Serializable