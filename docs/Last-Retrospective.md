# Sprint Retrospective, Iteration #3


## Log

|   User Story   | Task                                                                                                         | Task Assigned To | Estimated Effort (hour)    | Actual Effort (hour)       | Done (Y/n) | Notes |
| :------------: | :----------------------------------------------------------------------------------------------------------- | :--------------- | :------------------------- | :------------------------- | :--------: | :---: |
|     README     | Write README                                                                                                 | Vincent          | 1 hour                     | ?                          |     Y      |       |
|     Rooms      | Only teacher can get overview of all rooms and book rooms                                                    | Carlotta         | 1.5 hours                  | 1.5 hours                  |     Y      |       |
|    Courses     | Only teachers can create a course, delete it and enroll students                                             | Carlotta         | 1.5 hours                  | 1.5 hours                  |     Y      |       |
|    Courses     | Update and deletion of courses                                                                               | Matteo, Vincent  | 2 hour                     | 4 hour                     |     Y      |       |
|    Courses     | Allow teachers to delete students and fellow teachers from a course                                          | Matteo           | 2 hour                     | 2 hour?                    |     Y      |       |
|    Courses     | Allow students to un-enroll from a course                                                                    | Matteo           | 2 hour                     | 2 hour ?                   |     Y      |       |
|    Courses     | Add logic to take the netID from the JWT for student course enrollments                                      | Eva              | 0.5 hours                  | 0.5 hours                  |     Y      |       |
|    Courses     | Check and test that students and teachers who are already enrolled in a course can't do it again             | Eva              | 0.5 hours                  | 0.5 hours                  |     Y      |       |
|    Courses     | Check that only teachers can be added as teachers                                                            | Eva              | 1 hour                     | 1 hour                     |     Y      |       |
|    Courses     | Test the enrollment logic                                                                                    | Eva              | 1.5 hours                  | 1.5 hours                  |     Y      |       |
|      Test      | Tested `@PreAuthorize` annotation for role of teachers                                                       | Carlotta         | 1 hour                     | 1 hour                     |     Y      |       |
|      Test      | Refactor tests to be independent of the data in DB                                                           | Vincent          | 1 hour                     | 0.75 hour                  |     Y      |       |
|      Test      | Write tests for jwt authentication, mainly the filter in authentication micro-service                        | Mathan           | 2 hour                     | 2.5 hour                   |     Y      |       |
|      Test      | Write tests for all the entities to get the branch coverage up                                               | Annabel          | 1.5 hour                   | 1.5 hour                   |     Y      |       |
|      Test      | Write tests for authentication filter in all micro-services (Other than authentication micro-service)        | Mathan           | 2 hour                     | 2 hour                     |     Y      |       |
|      Test      | Write tests for authentication recourse                                                                      | Annabel          | 1.5 hour                   | 2 hour                     |     Y      |       |
|      Test      | Refactor tests to separate the integration tests                                                             | Annabel          | 1 hour                     | 2.5 hour                   |     Y      |       |
| Authentication | Write a filter for micro-services (other than Authentication micro-service) to authenticate incoming request | Mathan           | 2 hour                     | 2 hour                     |     Y      |       |
| Authentication | Create endpoints for validating JWTs in behalf of other micro-services                                       | Mathan           | 1 hour                     | 1 hour                     |     Y      |       |
| Authentication | Clean up the code, renaming files, refactoring and removing unused methods                                   | Annabel          | 1 hour                     | 1 hour                     |     Y      |       |
|    Cleanup     | Code sweep, build scripts, TODO, junk removal                                                                | Vincent          | 1 hour                     | 1 hour                     |     Y      |       |
|    Cleanup     | Prettify the merge request and issues                                                                        | Eva and Vincent  | 2 hour                     | 1 hour                     |     Y      |       |
|     Video      | Shot the individual clips                                                                                    | Everyone         | 6 hours                    | 6 hours                    |     Y      |       |
|     Video      | Editing the video clips                                                                                      | Annabel, Vincent | (4 + 2) hours Respectively | (4 + 2) hours Respectively |     Y      |       |

## Main Problems Encountered

### Problem 1

- Description: Database is frequently overloaded. `Too Many Connections` and fails build
- Reaction: Switch database providers (we didn't (yet)) / self host database

### Problem 2

- Description: One authentication test case fails when run as a test suite due to security context initialization issues.
- Reaction: Solved by initializing SecurityContext to have null authentication before every test.