package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentEntityTest {

    transient Student student;
    transient Student studentEmptyConstructor;
    transient String netID;

    @BeforeEach
    void setUp() {
        netID = "512";
        student = new Student(1, netID, 1);
        studentEmptyConstructor = new Student();
    }

    @Test
    void constructor() {
        assertNotNull(student);
        assertNotNull(studentEmptyConstructor);
    }

    @Test
    void getIndex() {
        assertEquals(1, student.getIndex());
    }

    @Test
    void setIndex() {
        student.setIndex(2);
        assertEquals(2, student.getIndex());
    }

    @Test
    void getNetID() {
        assertEquals(netID, student.getNetID());
    }

    @Test
    void setNetID() {
        student.setNetID("kruidnoten");
        assertEquals("kruidnoten", student.getNetID());
    }

    @Test
    void getCourseCode() {
        assertEquals(1, student.getCourseCode());
    }

    @Test
    void setCourseCode() {
        student.setCourseCode(2);
        assertEquals(2, student.getCourseCode());
    }

    @Test
    void testEquals() {
        Object duplicate = new Student(1, netID, 1);
        assertEquals(student, duplicate);
        Object duplicate2 = new Student(2, netID, 1);
        assertNotEquals(student, duplicate2);
        Object duplicate3 = new Student(1, "514", 1);
        assertNotEquals(student, duplicate3);
        Object duplicate4 = new Student(1, netID, 2);
        assertNotEquals(student, duplicate4);
        assertNotEquals(null, student);
        assertNotEquals(student, null);
        assertNotEquals(student, new Object());
        assertEquals(student, student);
    }

    @Test
    void testHashCode() {
        assertNotNull(student.hashCode());
    }
}