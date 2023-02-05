# Week 2 - Sprint Retrospective, Iteration #2

## Log

|   User Story   | Task                                                                                            | Task Assigned To | Estimated Effort (hour) | Actual Effort (hour) | Done  | Notes |
| :------------: | :---------------------------------------------------------------------------------------------- | :--------------- | :---------------------- | :------------------- | :---: | :---: |
|     Rooms      | Created a room entity                                                                           | Carlotta         | 1.5 hours               | 1.5 hours            |   Y   |  N/A  |
|     Rooms      | Made endpoints for room micro-service                                                           | Mathan, Carlotta | 3 hours                 | 3.5 hours            |   Y   |  N/A  |
|     Rooms      | Tests for room entity                                                                           | Mathan, Carlotta | 1 hour                  | 1 hour               |   Y   |  N/A  |
|     Rooms      | Tests for room micro-service endpoints                                                          | Mathan, Carlotta | 2 hours                 | 3 hours              |   Y   |  N/A  |
|    Courses     | Allow teacher to create courses                                                                 | Yuanze           | 2 hours                 | 1.5 hours            |   Y   |  (1)  |
|    Courses     | Create checks before course creation                                                            | Yuanze           | 1 hour                  | 1 hour               |   Y   |  N/A  |
|    Courses     | Introduce MockWebServer and its configuration to mock APIs                                      | Yuanze           | 1 hour                  | 2 hours              |   Y   |  N/A  |
|    Courses     | Testing the both good weather scenario and bad weather scenarios                                | Yuanze           | 2 hour                  | 2.5 hours            |   Y   |  N/A  |
|    Courses     | Creating tests instances for the entities                                                       | Annabel & Yuanze | 1 hour                  | 1 hours              |   Y   |  N/A  |
|    Courses     | Make controllers for the overviews and added some queries to the repository                     | Annabel          | 1.5 hour                | 2.5 hours            |   Y   |  N/A  |
|    Courses     | Test several controllers with Mockito                                                           | Annabel          | 1.5 hour                | 2 hours              |   N   |  (5)  |
|    Courses     | Create controller for course and added basic endpoints                                          | Matteo           | 1 hour                  | 1 hour               |   Y   |  N/A  |
|    Courses     | Allow teacher to update and delete courses                                                      | Matteo           | 2 hours                 | 2.5 hours            |   N   |  (4)  |
|    Courses     | Add courses entities and repositories                                                           | Eva              | 1 hour                  | 2 hours              |   Y   |  N/A  |
|    Courses     | Allow students to see what courses are open for enrollment and enroll themselves                | Eva              | 2 hours                 | 2 hours              |   Y   |  (2)  |
|    Courses     | Mock testing for enrolling logic                                                                | Eva              | 1 hour                  | 1 hour               |   N   |  N/A  |
|    Courses     | Allow teachers to enroll students into courses and add themselves and other teachers to courses | Eva              | 2 hours                 | 3 hours              |   Y   |  (3)  |
| Authentication | Creating tests instances (and mocking)                                                          | Annabel          | 1 hour                  | 1.5 hours            |   N   |  (5)  |
|  Fundamentals  | Create sub-projects for each micro-service                                                      | Matteo           | 2 hours                 | 3 hours              |   Y   |  N/A  |

## Link under notes
1. Not yet adding teacher to courses
2. Connecting to authentication micro-service is necessary for extraction of the netID
3. Also needs authentication micro-service connection for netID extraction
4. Missing checks and testing
5. Can be tested more

## Main Problems Encountered

### Problem 1

- Description: Updating the database when you clear a room or book a room didn't work.
- Reaction: Use method saveAndFlush to update database values.

### Problem 2

- Description: Deletion and updates to courses did not cascade to other tables
- Reaction: Modified database schema to allow for cascading

### Problem 3

- Description: Students enrolling themselves into courses need to be identified by their netID, and authentication micro-service connection is needed for that
- Reaction: Leave a TODO for it and hardcode a netID for it at the moment

### Problem 5

- Description: Testing API without running other micro-services which might be still under construction.
- Reaction: MockWebService with dispatcher configuration

### Problem 6

- Description: Communication between the micro-services in non-blocking (Async) manners
- Reaction: WebClient (both blocking and non-blocking). Although non-blocking is still experimental as of now.

### Problem 7

- Description: Current project file structure was not adequate for a micro-service architecture
- Reaction: Refactoring the project into different modules for each service

## Adjustments for the next Sprint Plan

- Check CheckStyle and PMD errors before pushing to remote
