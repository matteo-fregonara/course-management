# Course management system

- Tuesday deadline (2020/11/17) for the finalized requirement
- Finalize the requirements, send back to him by next Wednesday (2020/11/18)
- Three weeks to finish up the project

### Schema



### Teacher
- create new courses
- course code
- max size students
- page description

### Room
- Assign room for course
- room has fixed capacity
- make sure the room fits all the students
- No timetable needed, but take it out of the available rooms
- Room is going to be tied to the course
- No room timetable scheduling

### Students
- enroll (not by default)
- not automatically open for enrollment
- a.l.a enough spaces are left

- Overview of the course
- How many teacher it has
- How many students are in there
- All the rest of the info (rooms etc)

### Overview
- Teacher should be able to see which rooms are available
- Students and teachers should have an overview of their courses

### Requirements
- Each user of the system has an NetID and the password to authenticate
- Creator of the course is the admin of the course
- Teacher creates course is the user with the most right
- Role assignment (Nothing concrete yet)
- Scalability
- Ability to add more users and features

### Tooling
- Spring security
- Spring boot application

### Functional and non functional requirements
- Something to ponder about

### Micro-service
- Rest api calls
- No client side applications
