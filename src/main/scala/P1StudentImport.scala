
import com.mongodb.client.MongoClients
import org.bson.Document

import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import scala.io.Source
import scala.jdk.CollectionConverters._

object P1StudentImport {

  def main(args: Array[String]): Unit = {

    val students = new java.util.ArrayList[Document]()

    val file = Source.fromFile("hw2-data.csv", "UTF-8")

    try {
      for (line <- file.getLines().drop(1)) {

        val parts = line.trim.split(",", 5)

        val studentId = parts(0).toInt
        val firstName = parts(1)
        val lastName = parts(2)

        val birthDate = Date.from(
          LocalDate.parse(parts(3))
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
        )

        val lastComma = parts(4).lastIndexOf(",")

        val coursesText = parts(4).substring(0, lastComma)
        val gpaText = parts(4).substring(lastComma + 1)

        val courseIds = coursesText
          .stripPrefix("[")
          .stripSuffix("]")
          .split(",")
          .map(_.trim.toInt)
          .toList

        val gpa = gpaText.toDouble

        val student = new Document("_id", studentId)
          .append("firstName", firstName)
          .append("lastName", lastName)
          .append("dateOfBirth", birthDate)
          .append("courseIds", courseIds.map(Int.box).asJava)
          .append("gpa", gpa)

        students.add(student)
      }
    } finally {
      file.close()
    }

    println("\n--- Part 1: Import Students to MongoDB ---\n")

    println(students.get(0).toJson())
    println("Total students: " + students.size())

    val client = MongoClients.create("mongodb://localhost:27017/")

    try {
      val db = client.getDatabase("university")
      val collection = db.getCollection("students", classOf[Document])

      println("\nConnected to MongoDB\n")

      collection.drop()

      val result = collection.insertMany(students)

      println("Inserted students: " + result.getInsertedIds.size())

      collection.createIndex(new Document("courseIds", 1))
      collection.createIndex(new Document("gpa", 1))

      println("Indexes:")

      for (index <- collection.listIndexes().asScala) {
        println(index.getString("name"))
      }

      val writer = new java.io.PrintWriter("students.json", "UTF-8")

      try {
        for (student <- students.asScala) {

          val data = new Document(student)

          val date = student.getDate("dateOfBirth")
          data.put("dateOfBirth", date.toInstant.toString)

          writer.println(data.toJson())
        }
      } finally {
        writer.close()
      }

      println("students.json created successfully")

    } finally {
      client.close()
    }
  }
}
