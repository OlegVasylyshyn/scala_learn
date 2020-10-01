import scala.collection.mutable.ArrayBuffer
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import scala.io.StdIn.readLine


object StudentRecord extends App {
  val student = new StudentRecord
  val fileName = "students.txt"
  while (true) {
    val choice = student.menu
    choice match {
      case 1 =>
        if (student.addStudent)
          println("Student added")
        else
          println("Record not saved")
      case 2 =>
        println("Show students\n")
        student.showStudents
      case 3 =>
        println("Delete a student\n")
        student.deleteStudent
      case 4 =>
        println("Exit App")
        System.exit(0)
    }
  }
}

class StudentRecord {

  def addStudent: Boolean = {
    var isRecordAdded = false
    val student = new Student()

    println("Enter Student Name")
    student.name = readLine()
    println("Enter Student Age")
    student.age = readLine().toInt
    println("Enter Student average mark")
    student.mark = readLine().toDouble

    var fileInputStream: FileInputStream = null;
    var stdLstBfr:ArrayBuffer[Student]=ArrayBuffer()
    try {
      fileInputStream = new FileInputStream(StudentRecord.fileName)
      val inputStream = new ObjectInputStream(fileInputStream);
      stdLstBfr = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
      stdLstBfr += student
    } catch {
      case e: Exception =>
        stdLstBfr +=student
    }

    val outputStream = new ObjectOutputStream(new FileOutputStream(StudentRecord.fileName));
    outputStream writeObject (stdLstBfr)
    isRecordAdded = true

    return isRecordAdded
  }

  def showStudents {

    val inputStream = new ObjectInputStream(new FileInputStream(StudentRecord.fileName));
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
      sort(stdLstBfr, "name")
    } else if (action == "by age") {
      sort(stdLstBfr, "age")
    } else if (action == "by mark") {
      sort(stdLstBfr, "mark")
    } else if (action == "main menu") {
      menu
    } else {
      println("Please chose an action")
    }
  }

  def sort(stud_list: ArrayBuffer[Student], mode: String): Unit = {
    val inputStream = new ObjectInputStream(new FileInputStream(StudentRecord.fileName));
    val stud_list = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]

    val l = stud_list.length
    var swapped: Boolean = true

    if (mode == "age") {
      while (swapped) {
        swapped = false
        for (i <- 0 until stud_list.length - 1) {
          if (stud_list(i).age > stud_list(i + 1).age) {
            var temp = stud_list(i)
            stud_list(i) = stud_list(i + 1)
            stud_list(i + 1) = temp
            swapped = true
          }
        }
      }
      println("Name, age, av.mark\n")
      for (stud <- stud_list) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
    } else if (mode == "mark") {
      while (swapped) {
        swapped = false
        for (i <- 0 until stud_list.length - 1) {
          if (stud_list(i).mark > stud_list(i + 1).mark) {
            var temp = stud_list(i)
            stud_list(i) = stud_list(i + 1)
            stud_list(i + 1) = temp
            swapped = true
          }
        }
      }
      println("Name, age, av.mark\n")
      for (stud <- stud_list) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
    } else if (mode == "name") {
      val sorted =stud_list.sortWith(_.name < _.name)
      println("Name, age, av.mark\n")
      for (stud <- sorted) {
        println(stud.name + " " + stud.age + " " + stud.mark)
      }
    }
  }

  def deleteStudent(): Unit = {

    val inputStream = new ObjectInputStream(new FileInputStream(StudentRecord.fileName));
    val stud_list = inputStream.readObject.asInstanceOf[ArrayBuffer[Student]]
    println("Name, age, av.mark")
    for (std <- stud_list) {
      println(std.name + " " + std.age + " " + std.mark)
    }
    while (true) {
      print("You can delete student:\n" +
        "    \"by name\" - delete by name\n" +
        "    \"by id\" - delete by id\n" +
        "    \"main menu\" - Go to main menu\n")
      print("chose an action: ")
      var action: String = readLine()
      var clone = stud_list.clone()
      if (action == "by name") {
        print("Type name: ")
        var name: String = readLine()
        for (std <- clone) {
          if (std.name.equals(name)) {
            stud_list.remove(stud_list.indexOf(std))
          }
        }
      } else if (action == "by id") {
        println("delete by id")
      } else if (action == "main menu") {
        menu
      } else {
        println("Please chose an action")
      }
      println("Name, age, av.mark")
      for (std <- stud_list) {
        println(std.name + " " + std.age + " " + std.mark)
      }
      val outputStream = new ObjectOutputStream(new FileOutputStream(StudentRecord.fileName));
      outputStream.writeObject(stud_list)
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
      case ex: NumberFormatException => println("Please Enter Valid Input")
    }
    return choice
  }
}

class Student() extends Serializable {
  var name = ""
  var age: Int = 0
  var mark = 0.0
}