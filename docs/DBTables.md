# Database Schemas

Generate the graph at: https://dbdiagram.io/d 

## Authentication Service

```
Table users {
    netID varchar(128) [pk, unique]
    password varchar(256)
    roles varchar(256) 
}
```

- Password up to 256 char hash
  - Although SHA256 has just 64 hex chars

## Courses

```
Table courses {
    course_code int(11) [pk]
    max_size int(11)
    user_friendly_name varchar(256)
    description varchar(256)
    can_enroll bool
    room_code int(11)
}

Table students_in_course {
    netID varchar(256)
    course_code int(11) [ref: > courses.course_code]
    index int(11) [pk, increment]
}

Table teachers_give_course {
    netID varchar(256)
    course_code int(11) [ref: > courses.course_code]
    index int(11) [pk, increment]
}
```

- Description up to 256 characters
- `course_code` represented by an int


## Rooms

```
Table rooms {
    room_code int(11) [pk, unique]
    user_friendly_name varchar(256) 
    max_size int(11)
    course_code int(11) [unique]
}
```

## Cheat sheet
- Creating references
- You can also define relationship separately
    - \> many-to-one; 
    - < one-to-many;
    - \- one-to-one.
