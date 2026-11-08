
import com.mongodb.client.MongoClients
import org.bson.Document

import scala.io.StdIn
import scala.util.Try

object P2CourseSearch {

  def main(args: Array[String]): Unit = {

    val client = MongoClients.create("mongodb://localhost:27017/")

    try {
      val db = client.getDatabase("university")
      val collection = db.getCollection("students", classOf[Document])

      println("\n--- Part 2: Find Students by Course ---\n")

      var courseId = 0
      var validInput = false

      while (!validInput) {

        print("Enter course ID: ")

        Try(StdIn.readLine().toInt).toOption match {

          case Some(value) =>
            courseId = value
            validInput = true

          case None =>
            println("Invalid input. Please enter an integer.")
        }
      }

      val results = collection.find(
        new Document("courseIds", courseId)
      ).sort(new Document("lastName", 1))

      var count = 0

      val cursor = results.iterator()

      try {
        while (cursor.hasNext) {

          val student: Document = cursor.next()

          println(
            s"ID: ${student.getInteger("_id")} | " +
              s"Name: ${student.getString("firstName")} " +
              s"${student.getString("lastName")} | " +
              s"GPA: ${student.getDouble("gpa")}"
          )

          count += 1
        }
      } finally {
        cursor.close()
      }

      println("Total matches: " + count)

      val command = new Document(
        "explain",
        new Document("find", "students")
          .append("filter", new Document("courseIds", courseId))
      )

      val explanation = db.runCommand(command)

      val winningPlan = explanation
        .get("queryPlanner", classOf[Document])
        .get("winningPlan", classOf[Document])

      println("\nIndex used: " +
        winningPlan.toJson().contains("IXSCAN"))

      println("\nWinning plan: " + winningPlan.toJson())

    } finally {
      client.close()
    }
  }
}
