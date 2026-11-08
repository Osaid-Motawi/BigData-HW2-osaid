
ThisBuild / scalaVersion := "2.13.18"

name := "BigData-HW2-osaid"

version := "1.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "4.2.0",
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.6.1",
  "org.mongodb" % "mongodb-driver-sync" % "5.5.1"
)
