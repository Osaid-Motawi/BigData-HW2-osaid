
import com.mongodb.client.MongoClients
import org.bson.Document

import scala.io.StdIn
import scala.util.Try

object P3GpaSearch {

  def main(args: Array[String]): Unit = {

    val client = MongoClients.create("mongodb://localhost:27017/")

    try {
      val db = client.getDatabase("university")
      val collection = db.getCollection("students", classOf[Document])

      println("\n--- Part 3: Find Students by GPA ---\n")

      var minGpa = 0.0
      var validInput = false

      while (!validInput) {

        print("Enter minimum GPA (0.0 - 4.0): ")

        Try(StdIn.readLine().toDouble).toOption match {

          case Some(value) if value >= 0.0 && value <= 4.0 =>
            minGpa = value
            validInput = true

          case Some(_) =>
            println("GPA must be between 0.0 and 4.0")

          case None =>
            println("Invalid input Please enter a number")
        }
      }

      val results = collection.find(
        new Document("gpa", new Document("$gt", minGpa))
      ).sort(new Document("gpa", -1))

      var count = 0

      val cursor = results.iterator()

      try {
        while (cursor.hasNext) {

          val student: Document = cursor.next()

          println( s"ID: ${student.getInteger("_id")} | " + s"Name: ${student.getString("firstName")} " + s"${student.getString("lastName")} | " + s"GPA: ${student.getDouble("gpa")}" )

          count += 1
        }
      } finally {
        cursor.close()
      }

      println("Total matches: " + count)

      println("\n--- Combined Search: Course and GPA ---\n")

      print("Do you want a combined search? (y/n): ")

      val choice = StdIn.readLine().trim.toLowerCase

      if (choice == "y") {

        var combinedCourseId = 0
        validInput = false

        while (!validInput) {

          print("Enter course ID: ")

          Try(StdIn.readLine().toInt).toOption match {

            case Some(value) =>
              combinedCourseId = value
              validInput = true

            case None =>
              println("Invalid input Please enter an integer")
          }
        }

        var combinedMinGpa = 0.0
        validInput = false

        while (!validInput) {

          print("Enter minimum GPA (0.0 - 4.0): ")

          Try(StdIn.readLine().toDouble).toOption match {

            case Some(value) if value >= 0.0 && value <= 4.0 =>
              combinedMinGpa = value
              validInput = true

            case Some(_) =>
              println("GPA must be between 0.0 and 4.0")

            case None =>
              println("Invalid input Please enter a number")
          }
        }

        val combinedResults = collection.find(
          new Document("courseIds", combinedCourseId)
            .append("gpa", new Document("$gt", combinedMinGpa))
        ).sort(new Document("gpa", -1))

        count = 0

        val combinedCursor = combinedResults.iterator()

        try {
          while (combinedCursor.hasNext) {

            val student: Document = combinedCursor.next()

            println(s"ID: ${student.getInteger("_id")} | " + s"Name: ${student.getString("firstName")} " + s"${student.getString("lastName")} | " + s"GPA: ${student.getDouble("gpa")}")

            count += 1
          }
        } finally {
          combinedCursor.close()
        }

        println("Total matches: " + count)

      } else {
        println("Combined search skipped")
      }

    } finally {
      client.close()
    }
  }
}
