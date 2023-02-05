package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeacherEntityTest {

    transient Teacher teacher;
    transient Teacher teacherEmptyConstructor;
    transient String netID;

    @BeforeEach
    void setUp() {
        netID = "512";
        teacher = new Teacher(1, netID, 1);
        teacherEmptyConstructor = new Teacher();
    }

    @Test
    void constructor() {
        assertNotNull(teacher);
        assertNotNull(teacherEmptyConstructor);
    }

    @Test
    void getIndex() {
        assertEquals(1, teacher.getIndex());
    }

    @Test
    void setIndex() {
        teacher.setIndex(2);
        assertEquals(2, teacher.getIndex());
    }

    @Test
    void getNetID() {
        assertEquals(netID, teacher.getNetID());
    }

    @Test
    void setNetID() {
        teacher.setNetID("kruidnoten");
        assertEquals("kruidnoten", teacher.getNetID());
    }

    @Test
    void getCourseCode() {
        assertEquals(1, teacher.getCourseCode());
    }

    @Test
    void setCourseCode() {
        teacher.setCourseCode(2);
        assertEquals(2, teacher.getCourseCode());
    }

    @Test
    void testEquals() {
        Object duplicate = new Teacher(1, netID, 1);
        assertEquals(teacher, duplicate);
        Object duplicate2 = new Teacher(2, netID, 1);
        assertNotEquals(teacher, duplicate2);
        Object duplicate3 = new Teacher(1, "514", 1);
        assertNotEquals(teacher, duplicate3);
        Object duplicate4 = new Teacher(1, netID, 2);
        assertNotEquals(teacher, duplicate4);
        assertNotEquals(null, teacher);
        assertNotEquals(teacher, null);
        assertNotEquals(teacher, new Object());
        assertEquals(teacher, teacher);
    }

    @Test
    void testHashCode() {
        assertNotNull(teacher.hashCode());
    }
}