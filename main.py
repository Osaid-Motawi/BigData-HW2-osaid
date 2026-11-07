
import json
from datetime import datetime
from pymongo import MongoClient
students = []

with open("hw2-data.csv", "r", encoding="utf-8") as file:
    next(file)

    for line in file:
        parts = line.strip().split(",", 4)

        student_id = int(parts[0])
        first_name = parts[1]
        last_name = parts[2]
        birth_date = datetime.strptime(parts[3], "%Y-%m-%d")


        courses_text, gpa_text = parts[4].rsplit(",", 1)

        courses_text = courses_text.strip("[]")
        course_ids = [int(course) for course in courses_text.split(",")]

        gpa = float(gpa_text)


        student = {
            "_id": student_id,
            "firstName": first_name,
            "lastName": last_name,
            "dateOfBirth": birth_date,
            "courseIds": course_ids,
            "gpa": gpa
        }

        students.append(student)

    print(students[0])
    print("Total students:", len(students))

    client = MongoClient("mongodb://localhost:27017/")

db = client["university"]
collection = db["students"]

print("Connected to MongoDB")


collection.drop()

result = collection.insert_many(students)

print("Inserted students:", len(result.inserted_ids))

collection.create_index("courseIds")
collection.create_index("gpa")

print("Indexes:")
for index in collection.list_indexes():
    print(index["name"])




with open("students.json", "w", encoding="utf-8") as file:
    for student in students:
        data = student.copy()
        data["dateOfBirth"] = data["dateOfBirth"].isoformat()
        file.write(json.dumps(data) + "\n")

print("students.json created successfully")


print("\n===Part 2: Search by Course===\n")

while True:
    try:
        course_id = int(input("Enter course ID: "))
        break
    except ValueError:
        print("Invalid input. Please enter an integer.")


results = collection.find({"courseIds": course_id}).sort("lastName", 1)

count = 0

for student in results:
    print(student["_id"], student["firstName"], student["lastName"], student["gpa"])
    count += 1

print("Total matches:", count)



explanation = collection.database.command(
    "explain",
    {"find": "students", "filter": {"courseIds": course_id}}
)

print("Index used:", "IXSCAN" in str(explanation["queryPlanner"]["winningPlan"]))


print("\n===Part 3: Search by GPA===\n")


while True:
    try:
        min_gpa = float(input("Enter minimum GPA (0.0 - 4.0): "))

        if 0.0 <= min_gpa <= 4.0:
            break
        else:
            print("GPA must be between 0.0 and 4.0")

    except ValueError:
        print("Invalid input. Please enter a number.")


results = collection.find({"gpa": {"$gt": min_gpa}}).sort("gpa", -1)

count = 0

for student in results:
    print(student["_id"], student["firstName"], student["lastName"], student["gpa"])
    count += 1

print("Total matches:", count)


print("\n===Combined Search===\n")

choice = input("Do you want a combined search? (y/n): ").lower()

if choice == "y":
    results = collection.find({
        "courseIds": course_id,
        "gpa": {"$gt": min_gpa}
    }).sort("gpa", -1)

    count = 0

    for student in results:
        print(student["_id"], student["firstName"], student["lastName"], student["gpa"])
        count += 1

    print("Total matches:", count)
