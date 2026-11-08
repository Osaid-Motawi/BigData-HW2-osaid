
import org.apache.spark.sql.SparkSession

object P4SparkAnalysis {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("StudentAnalysis")
      .master("local[*]")
      .config("spark.mongodb.read.connection.uri", "mongodb://localhost:27017/")
      .config("spark.mongodb.read.database", "university")
      .config("spark.mongodb.read.collection", "students")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val studentsDF = spark.read
      .format("mongodb")
      .load()

    println("Total students: " + studentsDF.count())
    studentsDF.printSchema()

    // ننشأ sql view
    studentsDF.createOrReplaceTempView("students")
    println("SQL View created successfully")

    // كتابة الاستعلاااامات

    //اول استعلام من 4.1
    println("\nStudents registered in Course 103:")

    val course103 = spark.sql("""
    SELECT _id, firstName, lastName, gpa
    FROM students WHERE array_contains(courseIds, 103)
    ORDER BY lastName """)

    course103.show(100,false)
    println("Total matches: " + course103.count())

  //ثاني استعلام من 4.1

    println("\nStudents with GPA greater than 3.5:")

    val highGpa = spark.sql("""
    SELECT _id, firstName, lastName, gpa
    FROM students WHERE gpa > 3.5
    ORDER BY gpa DESC """)

    highGpa.show(100, false)
    println("Total matches: " + highGpa.count())


    // اول استعلام من 4.2

    println("\nEnrollments:")

    val enrollmentsDF = spark.sql("""
    SELECT _id AS studentId,
    explode(courseIds) AS courseId, gpa
    FROM students """)

    enrollmentsDF.createOrReplaceTempView("enrollments")
    enrollmentsDF.show(10, false)


    // ثاني استعلام من 4.2

    println("\nCourse Statistics:")

    val courseStats = spark.sql("""
    SELECT courseId,
    COUNT(*) AS enrollmentCount,
    ROUND(AVG(gpa), 2) AS averageGpa,
    MIN(gpa) AS minGpa, MAX(gpa) AS maxGpa
    FROM enrollments GROUP BY courseId
    ORDER BY enrollmentCount DESC """)

    courseStats.show(false)


    // ثالث استعلام من 4.2


    println("\nStudent Course Distribution:")

    val courseDistribution = spark.sql("""
    SELECT size(courseIds) AS numberOfCourses,
    COUNT(*) AS studentCount FROM students
    GROUP BY size(courseIds)
    ORDER BY numberOfCourses """)

    courseDistribution.show(false)


    // اول استعلام من 4.3


    println("\nStudents by Birth Year:")

    val birthYearStats = spark.sql("""
    SELECT YEAR(dateOfBirth) AS birthYear,
    COUNT(*) AS studentCount,
    ROUND(AVG(gpa), 2) AS averageGpa FROM students
    GROUP BY YEAR(dateOfBirth)
    ORDER BY birthYear """)

    birthYearStats.show(false)


    // ثاني استعلام من 4.3

    // استخدمت DENSE_RANK عشان أرتب الطلاب حسب المعدل داخل كل مساق
    // إذا كان في طلاب عندهم نفس المعدل بأخذوا نفس الترتيب
    // وبعرض كل الطلاب اللي ترتيبهم من 1 إلى 3 حتى لو عددهم أكثر من 3

    println("\nTop Students in Each Course:")

    val topStudents = spark.sql("""
    SELECT courseId, studentId, gpa, studentRank
    FROM (
    SELECT courseId, studentId, gpa,
      DENSE_RANK() OVER (
      PARTITION BY courseId
      ORDER BY gpa DESC)
      AS studentRank FROM enrollments
    ) ranked
    WHERE studentRank <= 3
    ORDER BY courseId, studentRank, studentId """)

    topStudents.show(100, false)

    // ثالث استعلام من 4.3


    println("\nStudents have same Names:")

    val sameNames = spark.sql("""
    SELECT a._id AS student1,
           b._id AS student2,
           a.firstName,
           a.lastName,
           a.dateOfBirth AS dob1,
           b.dateOfBirth AS dob2
    FROM students a
    JOIN students b
    ON a.firstName = b.firstName
    AND a.lastName = b.lastName
    AND a._id < b._id
    ORDER BY a.firstName, a.lastName """)

    sameNames.show(false)

    // رابع استعلام من 4.3


    courseStats.createOrReplaceTempView("courseStats")

    println("\nStudents Above All Course Averages:")

    val aboveAverage = spark.sql("""
    SELECT e.studentId, e.gpa
    FROM enrollments e
    JOIN (
      SELECT courseId, AVG(gpa) AS courseAvg
      FROM enrollments
      GROUP BY courseId)
      c ON e.courseId = c.courseId
    GROUP BY e.studentId, e.gpa
    HAVING MIN(e.gpa - c.courseAvg) > 0
    ORDER BY e.studentId """)

    aboveAverage.show(100, false)


    // PART 4.4


    println("\nPart 4.4 - Save Course Statistics to MongoDB:")

    courseStats.write
      .format("mongodb")
      .mode("overwrite")
      .option("connection.uri", "mongodb://localhost:27017/")
      .option("database", "university")
      .option("collection", "course_stats")
      .save()

    val savedStats = spark.read
      .format("mongodb")
      .option("connection.uri", "mongodb://localhost:27017/")
      .option("database", "university")
      .option("collection", "course_stats")
      .load()

    println("\nReading Course Statistics from MongoDB:")
    savedStats.show(false)



    spark.stop()
  }
}
