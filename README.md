# Big Data — Homework 2 (Scala)

**Student:** Osaid Mohammed Abdalghafer Motawi  
**GitHub:** https://github.com/Osaid-Motawi/BigData-HW2-osaid

## Overview

This project implements MongoDB student-data operations and Apache Spark SQL analysis in **Scala**. The dataset contains 100 students, with integer IDs, names, dates of birth, integer course-ID arrays, and GPAs.

## Project structure

```text
BigData-HW2-osaid/
├── build.sbt
├── hw2-data.csv
├── .gitignore
├── .env.example
├── src/main/scala/
│   ├── P1StudentImport.scala
│   ├── P2CourseSearch.scala
│   ├── P3GpaSearch.scala
│   └── P4SparkAnalysis.scala
└── docs/
    └── HW2_Scala_Results.pdf
```

The `docs/` path is suggested for the evidence PDF. Generated `students.json` can be included if required by the assignment.

## Requirements

- JDK and sbt configured in IntelliJ IDEA
- Scala and Apache Spark, with compatible versions configured in `build.sbt`
- MongoDB Community Server running locally on port `27017`
- MongoDB Java synchronous driver and MongoDB Spark Connector, configured in `build.sbt`
- MongoDB Compass (optional, for visual verification)

Use the versions and dependencies already configured in the actual project; this README does not replace `build.sbt`.

## Setup and run

1. Start the local MongoDB server.
2. Place `hw2-data.csv` in the project root.
3. Open the sbt project in IntelliJ IDEA and allow dependencies to load.
4. Run these Scala objects **in order** from IntelliJ:
   - `P1StudentImport`: imports student data, creates indexes, and exports JSON.
   - `P2CourseSearch`: asks for a course ID and explains index use.
   - `P3GpaSearch`: asks for a minimum GPA and optionally performs a combined search.
   - `P4SparkAnalysis`: reads students with Spark SQL, runs analyses, and writes course statistics.
5. Inspect `university.students` and `university.course_stats` in MongoDB Compass.

## Part 1 — MongoDB import

Reads the CSV, creates the `university.students` collection, inserts **100 students**, and creates indexes on `courseIds` and `gpa`. The run output confirms the student count and index names (`_id_`, `courseIds_1`, `gpa_1`). The assignment also requires a `students.json` export.

## Part 2 — Search by course

Searches by integer course ID and displays matching students sorted by last name. Example: **Course 103 → 36 students**. MongoDB's query explanation shows `IXSCAN` and `Index used: true`.

## Part 3 — Search by GPA

Finds students with GPA **strictly greater** than the provided threshold and sorts by descending GPA. Example: **GPA > 3.5 → 49 students**. The optional combined search shown in the screenshots uses **Course 101 and GPA > 3.6 → 24 students**.

## Part 4 — Apache Spark SQL

Loads the MongoDB students collection into a Spark DataFrame, prints its schema, and creates a temporary SQL view. The analysis includes:

- Course 103 enrollment and students with GPA > 3.5 (`array_contains`)
- Exploding course IDs into enrollment rows (`explode`)
- Course enrollment counts, average GPA, minimum and maximum GPA
- Birth-year statistics and course distribution
- Ranking students by course (`DENSE_RANK` window function)
- Finding matching student names and students above course averages
- Writing the `course_stats` results back to MongoDB and reading them for verification

### Example course statistics

| Course | Enrollments | Average GPA | Min GPA | Max GPA |
|---|---:|---:|---:|---:|
| 101 | 48 | 3.56 | 3.1 | 3.9 |
| 105 | 47 | 3.50 | 3.1 | 3.9 |
| 104 | 44 | 3.59 | 3.1 | 3.9 |
| 102 | 43 | 3.50 | 3.1 | 3.9 |
| 103 | 36 | 3.39 | 3.1 | 3.9 |

The `university.course_stats` collection contains **5 documents**.

## Configuration and Git safety

This project currently shows local MongoDB connections (`mongodb://localhost:27017/`) in the Scala source. A `.env.example` file is provided as a safe configuration template; **the Scala code does not automatically load `.env`** unless environment-variable support is implemented. Never commit a real `.env`, passwords, access tokens, private keys, or credential-bearing connection strings.

The `.gitignore` excludes `.env` and typical IDE/build artifacts. If a secret was committed in the past, adding `.gitignore` alone does not remove it from Git history: rotate the secret and clean the history as appropriate.

## Evidence

See `docs/HW2_Scala_Results.pdf` for the cover page and **17 screenshots** documenting all four parts and MongoDB Compass verification.
