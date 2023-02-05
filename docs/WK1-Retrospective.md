# Week 1 - Sprint Retrospective, Iteration #1

## Log

|   User Story   | Task                                      | Task Assigned To         | Estimated Effort (hour) | Actual Effort (hour) | Done (Y/n) | Notes |
| :------------: | :---------------------------------------- | :----------------------- | :---------------------- | :------------------- | :--------: | :---: |
| Authentication | Basic skeleton for JWT                    | Carlotta, Mathan, Eva    | 3 hours                 | 3 hours              |     Y      |  N/A  |
| Authentication | Make a password encoder for userpasswords | Carlotta                 | 2 hours                 | 2 hours              |     Y      |  N/A  |
| Authentication | Improve the creation for JWT's secret key | Carlotta                 | 1 hour                  | 1.5 hours            |     Y      |  N/A  |
| Authentication | Role based authorization                  | Eva                      | 2 hours                 | 2 hours              |     Y      |  N/A  |
| Authentication | Connection to database                    | Mathan                   | 1 hour                  | 2 hours              |     Y      |  N/A  |
| Authentication | Creating tests instances                  | Annabel                  | 1 hour                  |                      |     n      |  N/A  |
|  Fundamentals  | Setting up DBs                            | Yuanze, Annabel & Matteo | 0.5 hour                | 2 hours              |     Y      |  N/A  |
|  Fundamentals  | Populating the DBs                        | Yuanze & Annabel         | 0.5 hour                | 0.5 hour             |     Y      |  N/A  |
|  Fundamentals  | Connect DBs to the application            | Matteo                   | 1/12 hour               | 1/12 hour            |     Y      |  N/A  |
|  Fundamentals  | House keeping (organize repo structure)   | Matteo                   | 1 hour                  | 1.5 hours            |     Y      |  N/A  |
|  Fundamentals  | Making schema for Microservices           | The whole team           | 1 hour                  | 1.5 hours            |     Y      |  N/A  |

## Main Problems Encountered

### Problem 1

- Description: The timezone of the database has to be defined.
- Reaction: Set the timezone of all database connections to CET to resolve the problem.

### Problem 2

- Description: Secret string for JWT generation was giving an error when we were trying to dynamically generate it.
- Reaction: Need to encode a self made string with an HS256 algorithm.

## Adjustments for the next Sprint Plan

- Write tests.
  - Writing tests are important because that we can spot the bugs and errors.
- Do the retrospectives before the meetings instead of after.
  - So that the TA knows what we are doing.
- Push to master before the meeting.
  - Same reasons as above.
