package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseTest {

    transient Course course;
    transient Course courseEmptyConstructor;
    transient List<Student> student;
    transient List<Teacher> teacher;
    transient String courseName;
    transient String description;

    @BeforeEach
    void setUp() {
        // Make a student and teacher for the courses
        student = new ArrayList<>();
        Student student1 = new Student(0, "id", 100);
        student.add(student1);
        teacher = new ArrayList<>();
        Teacher teacher1 = new Teacher(1, "net", 100);
        teacher.add(teacher1);

        courseName = "Course";
        description = "Description";
        course = new Course(1000, 200, courseName, description, false, 1101, student, teacher);
        courseEmptyConstructor = new Course();
    }

    @Test
    void constructorTest() {
        assertNotNull(course);
        assertNotNull(courseEmptyConstructor);
    }

    @Test
    void getStudent() {
        assertEquals(student, course.getStudent());
    }

    @Test
    void setStudent() {
        List<Student> newStudent = new ArrayList<>();
        course.setStudent(newStudent);
        assertEquals(newStudent, course.getStudent());
    }

    @Test
    void getTeacher() {
        assertEquals(teacher, course.getTeacher());
    }

    @Test
    void setTeacher() {
        List<Teacher> newTeacher = new ArrayList<>();
        course.setTeacher(newTeacher);
        assertEquals(newTeacher, course.getTeacher());
    }

    @Test
    void getCourseCode() {
        assertEquals(1000, course.getCourseCode());
    }

    @Test
    void setCourseCode() {
        course.setCourseCode(2000);
        assertEquals(2000, course.getCourseCode());
    }

    @Test
    void getMaxSize() {
        assertEquals(200, course.getMaxSize());
    }

    @Test
    void setMaxSize() {
        course.setMaxSize(250);
        assertEquals(250, course.getMaxSize());
    }

    @Test
    void getCourseName() {
        assertEquals(courseName, course.getCourseName());
    }

    @Test
    void setCourseName() {
        course.setCourseName("Name");
        assertEquals("Name", course.getCourseName());
    }

    @Test
    void getDescription() {
        assertEquals(description, course.getDescription());
    }

    @Test
    void setDescription() {
        course.setDescription("Test");
        assertEquals("Test", course.getDescription());
    }

    @Test
    void isCanEnroll() {
        assertFalse(course.isCanEnroll());
    }

    @Test
    void setCanEnroll() {
        course.setCanEnroll(true);
        assertTrue(course.isCanEnroll());
    }

    @Test
    void getRoomCode() {
        assertEquals(1101, course.getRoomCode());
    }

    @Test
    void setRoomCode() {
        course.setRoomCode(505);
        assertEquals(505, course.getRoomCode());
    }

    @Test
    void testEquals() {
        assertTrue(course.equals(course));
        Object newCourse = new Course(1000, 200, courseName, description,
                false, 1101, student, teacher);
        assertTrue(course.equals(newCourse));
        Object newCourse2 = new Course(1001, 200, courseName, description,
                false, 1101, student, teacher);
        assertFalse(course.equals(newCourse2));
        Object newCourse3 = new Course(1000, 202, courseName, description,
                false, 1101, student, teacher);
        assertFalse(course.equals(newCourse3));
        Object newCourse4 = new Course(1000, 200, "Courses wrong", description,
                false, 1101, student, teacher);
        assertFalse(course.equals(newCourse4));
        Object newCourse5 = new Course(1000, 200, courseName, "Wrong",
                false, 1101, student, teacher);
        assertFalse(course.equals(newCourse5));
        Object newCourse6 = new Course(1000, 200, courseName, description,
                false, 1102, student, teacher);
        assertFalse(course.equals(newCourse6));
        Object newCourse7 = new Course(1000, 200, courseName, description,
                true, 1101, student, teacher);
        assertFalse(course.equals(newCourse7));
        assertFalse(course.equals(new Object()));
        assertNotEquals(course, null);
    }

    @Test
    void testHashCode() {
        assertNotNull(course.hashCode());
    }
}