package nl.tudelft.sem.template.entities;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomTest {
    transient Room roomDefault;
    transient Room roomDummy;
    transient Room roomNoCourse;

    @BeforeEach
    void setUp() {
        roomDefault = new Room(5, "Default", 12, 42);
        roomDummy = new Room();
    }


    @Test
    void constructorTest() {
        assertNotNull(roomDefault);
    }

    @Test
    void getRoomCode() {
        assertEquals(5, roomDefault.getRoomCode());
    }

    @Test
    void setRoomCode() {
        roomDefault.setRoomCode(123);
        assertEquals(123, roomDefault.getRoomCode());
    }

    @Test
    void getName() {
        assertEquals("Default", roomDefault.getName());
    }

    @Test
    void setName() {
        roomDefault.setName("newName");
        assertEquals("newName", roomDefault.getName());
    }

    @Test
    void getMaxSize() {
        assertEquals(12, roomDefault.getMaxSize());
    }


    @Test
    void setMaxSize() {
        roomDefault.setMaxSize(13);
        assertEquals(13, roomDefault.getMaxSize());
    }

    @Test
    void getCourseCode() {
        assertEquals(42, roomDefault.getCourseCode());
    }


    @Test
    void setCourseCode() {
        roomDefault.setCourseCode(19);
        assertEquals(19, roomDefault.getCourseCode());
    }

    @Test
    void nullCourseCheck() {
        roomNoCourse = new Room(0, "No Course", 95, null);
        assertNull(roomNoCourse.getCourseCode());
    }


    @Test
    void testEquals() {
        assertFalse(roomDefault.equals(new Object()));
        assertNotEquals(roomDefault, null);

        assertTrue(roomDefault.equals(roomDefault));
        Object newRoom = new Room(5, "Default", 99, 0);
        assertTrue(roomDefault.equals(newRoom));
        Object diffRoom = new Room(5, "Different Room", 99, 0);
        assertFalse(roomDefault.equals(diffRoom));
        Object diffRoom2 = new Room(6, "Different Room", 99, 0);
        assertFalse(roomDefault.equals(diffRoom2));
    }

    @Test
    void testHashCode() {
        assertNotNull(roomDefault.hashCode());
    }


}
