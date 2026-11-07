# Student Records with MongoDB and Spark SQL

**Big Data — Homework 2**

**Student:** Osaid Mohammed Abdalghafer Motawi  
**Project:** Student Records with MongoDB and Spark SQL

## Project Overview

This project processes and analyzes 100 student records using MongoDB and Apache Spark SQL.

The project has two main stages:

1. **MongoDB:** Import student data, create indexes, and perform interactive searches.
2. **Apache Spark SQL:** Read student records from MongoDB and perform analytical queries.

## Technologies Used

- Python 3
- PyMongo
- MongoDB
- Scala 2.13.18
- Apache Spark 4.2.0
- MongoDB Spark Connector 10.6.1
- sbt
- IntelliJ IDEA

## Project Structure

```text
BigData-HW2-osaid/
├── mongo.py
├── hw2-data.csv
├── students.json
├── build.sbt
├── README.md
└── src/
    └── main/
        └── scala/
            └── SparkAnalysis.scala
```

## Part 1 — Importing and Indexing

The Python program reads `hw2-data.csv` and converts each student record into a MongoDB document with the correct data types.

The records are inserted into:

- Database: `university`
- Collection: `students`

Indexes are created on `courseIds` and `gpa`.

**Result:** 100 student records inserted successfully.

## Part 2 — Search by Course

The program accepts a course ID from the user and retrieves all students enrolled in that course.

Results are sorted by last name, and MongoDB's execution plan is checked for index usage.

**Example result:**

- Course ID: 101
- Matching students: 48
- Index used: True

## Part 3 — Search by GPA

The program accepts a minimum GPA between 0.0 and 4.0 and displays students with a higher GPA in descending order.

It also supports an optional combined search using both course ID and minimum GPA.

**Example result:**

- Minimum GPA: 3.5
- Matching students: 49

## Part 4 — Spark SQL Analysis

Apache Spark reads the student collection directly from MongoDB using the MongoDB Spark Connector.

The DataFrame is registered as a temporary SQL view named `students`.

### 4.1 Basic Queries

- Retrieve students enrolled in course 103.
- Retrieve students with GPA greater than 3.5.

### 4.2 Array Operations and Statistics

- Use `explode(courseIds)` to create an `enrollments` view.
- Calculate enrollment count, average GPA, minimum GPA, and maximum GPA for each course.
- Count students enrolled in one, two, or three courses.

### 4.3 Advanced SQL Queries

- Calculate student count and average GPA by birth year.
- Rank students within each course using `DENSE_RANK()`.
- Find students with matching first and last names.
- Find students whose GPA is higher than the average GPA of every course they are enrolled in.

**Handling ties:** Students with equal GPAs receive the same rank. All students in the first three ranks are included, even if this produces more than three students per course.

### 4.4 Saving Results to MongoDB

The course statistics are saved to a new MongoDB collection named `course_stats`.

The collection is then read back using Spark to verify that the results were stored successfully.

**Result:** Five course statistics records were saved and read successfully.

## Course Statistics

| Course ID | Students | Average GPA | Min GPA | Max GPA |
|---|---:|---:|---:|---:|
| 101 | 48 | 3.56 | 3.1 | 3.9 |
| 105 | 47 | 3.50 | 3.1 | 3.9 |
| 104 | 44 | 3.59 | 3.1 | 3.9 |
| 102 | 43 | 3.50 | 3.1 | 3.9 |
| 103 | 36 | 3.39 | 3.1 | 3.9 |

## How to Run

### Step 1 — Start MongoDB

Make sure the local MongoDB server is running at:

`mongodb://localhost:27017/`

### Step 2 — Run the Python Program

Install PyMongo:

```bash
pip install pymongo
```

Run:

```bash
python mongo.py
```

Enter a course ID and minimum GPA when prompted.

### Step 3 — Run Spark Analysis

Open the project in IntelliJ IDEA with the Scala and sbt environment configured.

Make sure MongoDB is running and the `students` collection has been populated.

Run the `SparkAnalysis` main class.

The program will execute the Spark SQL queries, display their results, and save the course statistics to MongoDB.

**Note:** On Windows, local Spark may require Hadoop native utilities to be configured.

## MongoDB vs. Spark SQL

MongoDB is used for storing student documents and performing indexed searches based on course IDs and GPA.

Spark SQL is used for analytical operations such as grouping, aggregations, joins, array expansion, and ranking.

Both systems operate on the same student dataset but serve different purposes.

## Conclusion

This assignment demonstrates how MongoDB and Apache Spark SQL can work together to store, retrieve, and analyze student data.

All 100 student records were imported successfully, the MongoDB searches were tested, and the Spark SQL analysis was completed.