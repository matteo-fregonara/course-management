# CSE2115 - Project

## Group 6

| Name                    | Email                            |
| ----------------------- | -------------------------------- |
| Annabel Simons          | A.Simons@student.tudelft.nl      |
| Carlotta Lichtenauer    | C.Lichtenauer@student.tudelft.nl |
| Eva Noritsyna           | E.Noritsyna@student.tudelft.nl   |
| Mathanrajan Sundarrajan | M.Sundarrajan@student.tudelft.nl |
| Matteo Fregonara        | M.Fregonara@student.tudelft.nl   |
| Yuanze* Xiong           | Y.Xiong-3@student.tudelft.nl     |

\* aka Vincent 

## Course Management System
This is the course management system by Group 6. This solution provides an overview the courses that the user participates. It allows students to enroll courses and teachers to create / modify / manage / delete courses. More details below.

We are using the micro-service architecture, with namely three micro-services.
- Room micro-service
- Course micro-service
- Authentication micro-service

Tech used: Spring Boot, Spring Security and MySQL database.

## Micro-services in depth

Each of the micro-service makes up as part of the solution and can be run on different instances (with correct configurations).

### Authentication
- Stores the detailed user information
- Issues access tokens after successful authentication
- Each token has one specific role for the authorization purposes
  - There are currently two roles: teacher and student
- Authenticate and authorize the request from other micro-services
- Tokens are being passed as part of the request header

### Course
- Stores detailed course information
  - Metadata (Description, size, etc.)
  - Students / teachers in the course
- Authenticate and authorize each request
- Communicate room micro-service to verify / propagate requests
- Students and teachers may (un)enroll themselves
- Students can get an overview of the courses they are enrolled in
- Teachers can get an overview of all courses (and limit to the course that they are teaching)
- Teachers may create / manage / delete courses
  - Update the state of the course
  - Manage enrolled students
  - Modify the teaching staffs

### Room
- Stores detailed room information
- Shows empty rooms
- (Un)book rooms for courses
- Allow query for room status
- Teacher can get an overview of all rooms

## Interaction

1. Start all three micro-services
2. Users authenticate themselves using the authentication service and receive JWT token
3. Users head to other two micro-services to do their things
4. Room & course micro-services check the JWT header and fetch the user information from Authentication micro-service or reject the user


## Setup Guide

### Configuration
- Arrange three database schemas for each of the micro-service
  - Configure the correct database link in all three `application.properties` file.
- (If necessary) modify the ports of the micro-services
  - Authentication runs by default at 8081
  - Course at 8082
  - Room at 8083

### Running 
```
gradle bootRun
```

Or make use of the run button in the IDE

### Testing
```
gradle test
```

To generate a coverage report:
```
gradle jacocoTestCoverageVerification
```

And
```
gradle jacocoTestReport
```
The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open index.html in your browser to see the report. 

### Static analysis
```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

## Examples

Here are a few examples, refer to the APIs for all features.
Some APIs are called automatically by the application to perform various checks.

### To Login and authenticate

POST method to `Localhost:8081/authenticate` with the following JSON in body
```
{
  "username": <NetID here>,
  "password": <password here>
}
``` 

Once authenticated, the JWT token is issued in the response header for this session. 
Keep this JWT token for future use, __all__ functions require this token for authentication purposes. 
Include token at `Authentication` field of the request header to identify yourself.

### Find empty rooms before creating course

GET method to `Localhost:8083/rooms/findEmptyRooms`, all available rooms will be returned in the response body

Alternatively, GET method to `Localhost:8083/rooms/getAllRooms` to view all the rooms

### Create a course

Once authenticated, POST method to `Localhost:8082/courses/createCourse` with the following JSON in body
```
{
  "courseCode": <int>,
  "maxSize": <int>,
  "courseName": <String>,
  "description": <String>,
  "canEnroll": <true/false>,
  "roomCode": <int>
}
```

The teacher creating the course will be automatically enrolled as the teacher of this new course

### Remove a course

DELETE method to `Localhost:8082/courses/delete/<courseCodeHere>`

Removing a course will remove all the enrolled students / teachers from this course

The room will be also automatically cleared once a course is removed

### View details of a course
GET method to `Localhost:8082/courses/<course code here>`

### (Un)enroll

Once logged in, POST to `Localhost:8082/enroll/<courseCode>`, with request header field `Authentication` and JWT token as value

NetID is automatically extracted from the token in the request header.

## Appendix

### Current Logins

| NetID         | Password            | Role    |
| ------------- | ------------------- | ------- |
| yuanzexiong   | notmyactualpassword | Teacher |
| msundarrajan  | wachtwoord123       | Teacher |
| ffregonara    | goedenmorgen0!      | Student |
| enoritsyna    | hallo123            | Student |
| CLichtenauer  | doei123             | Student |
| annabelsimons | wachtwoord          | Student |

### API overview

#### Authentication (Port 8081)

| HTTP Method | URL             | Description                                          |  Role   | Note  |
| :---------: | --------------- | ---------------------------------------------------- | :-----: | :---: |
|    POST     | `/authenticate` | Authenticates users, issues token in response header |   All   |  (1)  |
|     GET     | `/validate`     | Other micro-services validate users                  |   All   |  (2)  |
|     GET     | `/getUserInfo`  | Other micro-services get user NetID and role         |   All   |  (2)  |
|     GET     | `/getAll`       | Get all users                                        | Teacher |       |

1. With `username` and `password` in the request body as JSON
2. With JWT token from the request header field `Authorization`

#### Course (Port 8082)

Reminder: All the features unique to `Teacher` requires JWT token of a teacher

#### Manage/View courses
| HTTP Method | URL                                    | Description                                        |  Role   | Note  |
| :---------: | -------------------------------------- | -------------------------------------------------- | :-----: | :---: |
|     GET     | `/courses/<courseCodeHere>`            | Get course info using code                         |   All   |       |
|     GET     | `/courses/getAllStudents`              | Get all the students that are enrolled in a course |   All   |       |
|     GET     | `/courses/student/getCourses/<NetID>`  | Courses with this students                         | Student |       |
|     GET     | `/courses/teacher/getAll`              | All courses Overview                               | Teacher |       |
|     GET     | `/courses/teacher/getAllSorted`        | All courses sorted Overview                        | Teacher |       |
|     GET     | `/courses/teacher/getCourses/<NetID>`  | Courses by this teacher                            | Teacher |       |
|    POST     | `/courses/createCourse`                | Create new course                                  | Teacher |  (1)  |
|   DELETE    | `/courses/delete/<courseCodeHere>`     | Delete existing course                             | Teacher |  (2)  |
|    POST     | `/courses/update`                      | Change course details                              | Teacher |  (3)  |

#### (Un)enroll courses
| HTTP Method | URL                                              | Description                         |  Role   | Note  |
| :---------: | ------------------------------------------------ | ----------------------------------- | :-----: | :---: |
|     GET     | `/enroll`                                        | Get courses you can enroll          |   All   |       |
|    POST     | `/enroll/<courseCode>`                           | Enroll a course with code           |   All   |  (4)  |
|   DELETE    | `/enroll/unenrollStudent/<courseCode>`           | Remove student from a course        | Student |  (4)  |
|    POST     | `/enroll/<studentID>/<courseCode>`               | Teacher enrolls a student to course | Teacher |       |
|    POST     | `/enroll/addTeacher/<courseCode>`                | Add a teacher to a course           | Teacher |       |
|   DELETE    | `/enroll/removeStudent/<studentID>/<courseCode>` | Remove student from a course        | Teacher |       |
|   DELETE    | `/enroll/removeTeacher/<teacherID>/<courseCode>` | Remove teacher from a course        | Teacher |       |

1. With following fields as in JSON in the request body
   - `courseCode`: `int`, (Mandatory)
   - `maxSize`: `int`, (Mandatory)
   - `courseName`: `String`,
   - `description`: `String`,
   - `canEnroll`: `boolean`, (`false` if not specified)
   - `roomCode`: `int` (Mandatory)
2. With the `courseCode` as part of URL
3. With __all__ the following fields as in JSON in the request body
   - `courseCode`: `int`,
   - `description`: `String`,
   - `maxSize`: `int`,
   - `canEnroll`: `boolean`
4. With JWT token in the request header field `Authentication`

#### Room (Port 8083)

| HTTP Method | URL                                       | Description                        |  Role   | Note  |
| :---------: | ----------------------------------------- | ---------------------------------- | :-----: | :---: |
|     GET     | `/rooms/findCourse/<courseCode>`          | Get room info with course code     |   All   |       |
|     GET     | `/rooms/checkFit/<roomCode>/<size>`       | Check if the size fits in the room |   All   |       |
|     GET     | `/rooms/maxSize/<roomCode>`               | Check size of the room             |   All   |       |
|     GET     | `/rooms/clearRoom/<roomCode>`             | Unassociate room with course       | Teacher |       |
|     GET     | `/rooms/findEmptyRooms`                   | Find all empty rooms               | Teacher |       |
|     GET     | `/rooms/getAllRooms`                      | Get all rooms                      | Teacher |       |
|     GET     | `/rooms/bookRoom/<roomCode>/<courseCode>` | Associate room with course         | Teacher |       |

### Suppression

1. We have PMD suppressions throughout the project, they are explained in the JavaDocs in the code
2. We have no CheckStyle suppressions
3. We have no excluded files in JaCoCo