# Homework 2: Student Records with MongoDB and Spark SQL

**Course:** Big Data  
**Student:** Osaid Mohammed Abdalghafer Motawi  
**GitHub:** https://github.com/Osaid-Motawi/BigData-HW2-osaid

## About the Project

In this homework, I worked with a CSV file containing 100 student records. I used Python and MongoDB to import and search the data, then used Scala and Apache Spark SQL to analyze the same records.

## Tools

- Python and PyMongo
- MongoDB and MongoDB Compass
- Scala 2.13.18
- Apache Spark 4.2.0
- MongoDB Spark Connector 10.6.1
- IntelliJ IDEA and sbt

## Part 1: Importing and Indexing

I read `hw2-data.csv` and converted each row into a student document. The fields include the student ID, name, date of birth, course IDs, and GPA. I stored the records in the `university` database, inside the `students` collection.

I inserted all **100 students**, created indexes on `courseIds` and `gpa`, and exported the parsed records to `students.json` (one JSON document per line).

## Part 2: Search by Course

I wrote a Python query that asks the user for a course ID, checks the input, and displays matching students sorted by last name. I also checked the MongoDB execution plan for `IXSCAN`.

Example: course **103** returned **36 students**, and the index check returned **True** (`courseIds_1`).

## Part 3: Search by GPA

I added a search for students with a GPA greater than a value entered by the user. The input is validated to be between 0.0 and 4.0, and the results are sorted by GPA from highest to lowest.

Example: GPA **greater than 3.5** returned **49 students**.

I also added an optional combined search using both course ID and minimum GPA. For example, course **102** with GPA **greater than 3.7** returned **13 students**.

## Part 4: Spark SQL Analysis

I connected Spark directly to the MongoDB `students` collection and registered the data as a temporary SQL view named `students`. I printed the schema and confirmed that `courseIds` is an array of integers and `dateOfBirth` is a timestamp.

### 4.1 Basic Queries

- Found students enrolled in course **103** (**36 students**).
- Found students with GPA greater than **3.5** (**49 students**).

These counts match the MongoDB queries.

### 4.2 Course Statistics

I used `explode(courseIds)` to create an `enrollments` view with one row for each student-course pair. Then I calculated the enrollment count, average GPA, minimum GPA, and maximum GPA for each course.

| Course ID | Students | Average GPA | Min GPA | Max GPA |
| --- | ---: | ---: | ---: | ---: |
| 101 | 48 | 3.56 | 3.1 | 3.9 |
| 105 | 47 | 3.50 | 3.1 | 3.9 |
| 104 | 44 | 3.59 | 3.1 | 3.9 |
| 102 | 43 | 3.50 | 3.1 | 3.9 |
| 103 | 36 | 3.39 | 3.1 | 3.9 |

I also counted students by how many courses they take:

- **1 course:** 8 students
- **2 courses:** 66 students
- **3 courses:** 26 students

### 4.3 More SQL Queries

I used Spark SQL to:

- Count students and calculate average GPA by birth year.
- Find the top three **ranks** in each course using `DENSE_RANK()`. Students with the same GPA get the same rank, so a course can have more than three students in the top three ranks.
- Find pairs of students with the same first and last name and show their IDs and birth dates.
- Find students whose GPA is higher than the unrounded average GPA of every course they are enrolled in.

### 4.4 Saving the Results

I saved the course statistics from Part 4.2 into a new MongoDB collection called `university.course_stats`. I then read the collection back using Spark to check that the data was saved correctly. The collection contains **5 records**, one for each course. I also checked the saved records in MongoDB Compass.

## Running the Project

1. Start MongoDB locally at `mongodb://localhost:27017/`.
2. Install PyMongo if needed: `pip install pymongo`.
3. Run the Python script (`mongo.py`) from the project folder. This imports the data and runs the interactive searches.
4. Open the project in IntelliJ IDEA and load the sbt dependencies from `build.sbt`.
5. Run `SparkAnalysis.scala` after the MongoDB `students` collection has been populated.

The Spark program uses local mode and reads from MongoDB through the Spark connector. On Windows, the local Spark setup may also need Hadoop native utilities configured.

## Results and Screenshots

- `mongo_results.txt` contains the Python/MongoDB query results.
- `spark_results.txt` contains the Spark SQL output.
- `HW2_Results_Screenshots.pdf` contains screenshots of the program outputs in order, including the MongoDB Compass verification of `course_stats`.
- `Screenshots` contains the original screenshots as image files.

## Files

| File or folder | Description |
| --- | --- |
| `mongo.py` | Python code for Parts 1–3 |
| `hw2-data.csv` | Original student data |
| `students.json` | Exported student documents |
| `build.sbt` | Scala and Spark dependencies |
| `src/main/scala/SparkAnalysis.scala` | Scala code for Part 4 |
| `mongo_results.txt` | MongoDB/Python results |
| `spark_results.txt` | Spark SQL results |
| `HW2_Results_Screenshots.pdf` | Screenshots of the results |
| `Screenshots` | Original screenshot images |
