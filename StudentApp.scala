import scala.collection.mutable.ArrayBuffer
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import scala.io.StdIn.readLine


object StudentApp extends App {
  val journal = new StudentJournal
  val fileName: String = "students.txt"
  while (true) {
    val choice = journal.menu
    choice match {
      case 1 =>
        if (journal.addStudent())
          println("Student added")
        else
          println("Record not saved")
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

  def addStudent(): Boolean = {
    var isRecordAdded = false

    println("Enter Student Name")
    val name: String = readLine()
    println("Enter Student Age")
    val age: Int = readLine().toInt
    println("Enter Student average mark")
    val mark: Double = readLine().toDouble

    val student = Student(name, age, mark)

    var fileInputStream: FileInputStream = null;
    var stdLstBfr:ArrayBuffer[Student]=ArrayBuffer()
    try {
      fileInputStream = new FileInputStream(StudentApp.fileName)
      val inputStream = new ObjectInputStream(fileInputStream);
      stdLstBfr = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
      stdLstBfr += student
    } catch {
      case e :Exception => stdLstBfr += student
    }

    val outputStream = new ObjectOutputStream(new FileOutputStream(StudentApp.fileName));
    outputStream writeObject (stdLstBfr)
    isRecordAdded = true

    isRecordAdded
  }

  def showStudents() {

    val inputStream = new ObjectInputStream(new FileInputStream(StudentApp.fileName));
    val stdLstBfr = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
    println("Name, age, av.mark")
    for (std <- stdLstBfr) {
      println(std.name + " " + std.age + " " + std.mark)
    }

    print("You can sort the list by:\n" +
      "    \"by name\" - Sort students list by name\n" +
      "    \"by age\" - Sort students list by age\n" +
      "    \"by mark\" - Sort student list by average mark\n" +
      "    \"main menu\" - Go to main menu\n")
    print("chose an action: ")
    var action: String = readLine()
    if (action == "by name") {
      sort("name")
    } else if (action == "by age") {
      sort("age")
    } else if (action == "by mark") {
      sort("mark")
    } else if (action == "main menu") {

    } else {
      println("Please chose an action")
    }
  }

  def sort(mode: String): Unit = {
    val inputStream = new ObjectInputStream(new FileInputStream(StudentApp.fileName));
    val studList = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]

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
      println("Name, age, av.mark\n")
      for (stud <- studList) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
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
      println("Name, age, av.mark\n")
      for (stud <- studList) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
    } else if (mode == "name") {
      val sorted =studList.sortWith(_.name < _.name)
      println("Name, age, av.mark\n")
      for (stud <- sorted) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
    }
  }

  def deleteStudent(): Unit = {

    val inputStream = new ObjectInputStream(new FileInputStream(StudentApp.fileName));
    val studList = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
    println("Name, age, av.mark")
    for (std <- studList) {
      println(std.name + " " + std.age + " " + std.mark)
    }
    while (true) {
      print("You can delete student:\n" +
        "    \"by name\" - delete by name\n" +
        "    \"by id\" - delete by id\n" +
        "    \"main menu\" - Go to main menu\n")
      print("chose an action: ")
      val action: String = readLine()
      val clone = studList.clone()
      if (action == "by name") {
        print("Type name: ")
        val name: String = readLine()
        for (std <- clone) {
          if (std.name.equals(name)) {
            studList.remove(studList.indexOf(std))
          }
        }
      } else if (action == "by id") {
        println("delete by id")
      } else if (action == "main menu") {
        
      } else {
        println("Please chose an action")
      }
      println("Name, age, av.mark")
      for (std <- studList) {
        println(std.name + " " + std.age + " " + std.mark)
      }
      val outputStream = new ObjectOutputStream(new FileOutputStream(StudentApp.fileName));
      outputStream.writeObject(studList)
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