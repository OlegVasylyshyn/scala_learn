import scala.io.StdIn
import scala.io.Source
import java.io._
import java.nio.file.{Paths, Files}
import scala.collection.mutable.ArrayBuffer

object studentApp {
  def loadStudents (students: String): Unit = {
    println("ID, Name, age, av.mark")
    for (line <- Source.fromFile(students).getLines) {
      println(line)
    }
    while (true) {
      print("You can sort the list by:\n" +
        "    \"by name\" - Sort students list by name\n" +
        "    \"by age\" - Sort students list by age\n" +
        "    \"by av. mark\" - Sort student list by average mark\n" +
        "    \"main menu\" - Go to main menu\n")
      print("chose an action: ")
      var action: String = StdIn.readLine()
      if (action == "sort by name") {
        sortByName
      }
      else if (action == "by age") {
        sortByAge(students)
      }
      else if (action == "by av. mark") {
        sortByAverageMark
      }
      else if (action == "main menu") {
        main(Array())
      }
      else {
        println("Please chose an action")
      }
    }
  }

  def sortByName: Unit = {
    println("Sort by Name")
  }

  def sortByAge (students: String): Unit = {
    println("Sorted by age")
    var stud_db = Source.fromFile(students).getLines.to(ArrayBuffer)
    var sorted_db = Array(Array[Any]())
    for (entry <- stud_db) {
      println(entry)
      var arr = entry.split(", ")
      var copy_arr = Array[Any](arr(0), arr(1), arr(2).toInt, arr(3))
      sorted_db ++= Array(copy_arr)
    }

    //    val l = sorted_db.length
    //    var swapped: Boolean = true
    //
    //    while (swapped) {
    //      swapped = false
    //      for (i <- 0 to l-2) {
    //        if (sorted_db(i)(2) > sorted_db(i + 1)(2)) {
    //          var temp = sorted_db(i)
    //          sorted_db(i) = sorted_db(i + 1)
    //          sorted_db(i + 1) = temp
    //          swapped = true
    //        }
    //      }
    //    }

    print("sorted")
  }

  def sortByAverageMark: Unit = {
    println("Sort by average mark")
  }

  def deleteStudent (students: String): Unit = {
    println("Delete student")
    var stud_db = Source.fromFile(students).getLines.to(ArrayBuffer)
    println("\"by id\" - Delete student by id")
    println("\"by name\" - Delete student by name")
    println("\"main menu\" - Go to main menu\n")
    var action: String = StdIn.readLine()
    if (action == "by id") {
      print("Type id - ")
      val id: Int = StdIn.readInt()
      deleteById(stud_db, id)
    }
    else if (action == "by name") {
      print("Type name - ")
      val name: String = StdIn.readLine()
      deleteByName(stud_db, name)
    }
    else if (action == "main menu") {
      main(Array())
    }
    else {
      println("Please chose an action")
    }
  }

  def deleteById (array: ArrayBuffer[String], id: Int): Unit = {
    var stud_db = array.clone()
    for (entry <- array) {
      //      println(entry)
      var arr = entry.split(", ")
      if (arr(0).equals(id.toString)) {
        stud_db.remove(stud_db.indexOf(entry))
      }
    }
    normalize(stud_db)
  }

  def deleteByName (array: ArrayBuffer[String], name: String): Unit = {
    var stud_db = array.clone()
    for (entry <- array) {
      //      println(entry)
      var arr = entry.split(", ")
      if (arr(1).equals(name)) {
        stud_db.remove(stud_db.indexOf(entry))
      }
    }
    normalize(stud_db)
  }

  def normalize (array: ArrayBuffer[String]): Unit = {
    for (entry <- array) {
      var arr = entry.split(", ").to(ArrayBuffer)
      if (array.indexOf(entry) > 0) {
        saveEntry("student_list.txt", arr.drop(1), "append")
      }
      else {
        saveEntry("student_list.txt", arr.drop(1), "write")
      }
    }
  }

  def addStudent (students: String): Unit = {
    print("Write student name - ")
    val name: String = StdIn.readLine()
    print("Write student age - ")
    val age: String = StdIn.readLine()
    print("Write student average mark - ")
    val av_mark: String = StdIn.readLine()
    val studArray = ArrayBuffer(name, age, av_mark)

    saveEntry(students, studArray, "append")
  }

  def exitApp: Unit = {
    println("Exit App")
    System.exit(0)
  }

  def saveEntry(filename: String, stud_entry: ArrayBuffer[String], mode: String): Unit = {
    val bool = Files.exists(Paths.get(filename))
    if (bool == true && mode == "append") {
      val students = Source.fromFile(filename).getLines.to(ArrayBuffer)
      val students_len = students.length
      val id: Int = students_len + 1
      stud_entry.prepend(id.toString)
      writeLine(filename, stud_entry, mode)
    }
    else {
      val id: Int = 1
      stud_entry.prepend(id.toString)
      writeLine(filename, stud_entry, mode)
    }


    def writeLine (filename: String, array: ArrayBuffer[String], mode: String): Unit = {
      var stud_str = stud_entry.mkString(", ")
      var line = stud_str + "\n"
      val file = new File(filename)
      if (mode == "append") {
        val bw = new BufferedWriter(new FileWriter(file, true))
        bw.write(line)
        bw.close()
      }
      else if (mode == "write") {
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(line)
        bw.close()
      }

    }

  }

  def main(args: Array[String]): Unit = {
    // your code

    while (true) {
      println("Student App")
      println("Actions: \"show students\" - show students list\n" +
        "      \"delete student\" - remove a student form the list\n" +
        "      \"add student\" - add a new student to the list\n" +
        "      \"exit app\" - exit the application")
      print("Chose action: ")
      var action: String = StdIn.readLine()
      val filename = "student_list.txt"
      if (action == "show students") {
        loadStudents(filename)
      }
      else if (action == "delete student") {
        deleteStudent(filename)
      }
      else if (action == "add student") {
        addStudent(filename)
      }
      else if (action == "exit app") {
        exitApp
      }
      else {
        println("Please chose an action")
      }
    }
  }
}
