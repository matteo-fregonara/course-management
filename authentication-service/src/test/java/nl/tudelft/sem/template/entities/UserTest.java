package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    transient User user;
    transient User user2;
    transient String netID;
    transient String password;
    transient String roles;

    @BeforeEach
    void setUp() {
        netID = "testUser";
        password = "password";
        roles = "STUDENT";
        user = new User(netID, password, roles);
        user2 = new User();
    }

    @Test
    void constructorTest() {
        assertNotNull(user);
    }

    @Test
    void getRoles() {
        assertEquals(roles, user.getRoles());
    }

    @Test
    void setRoles() {
        user.setRoles("TEACHER");
        assertEquals("TEACHER", user.getRoles());
    }

    @Test
    void setNetID() {
        user.setNetID("netID");
        assertEquals("netID", user.getNetID());
    }

    @Test
    void setPassword() {
        user.setPassword("pass");
        assertEquals("pass", user.getPassword());
    }

    @Test
    void getNetID() {
        assertEquals(netID, user.getNetID());
    }

    @Test
    void getPassword() {
        assertEquals(password, user.getPassword());
    }

    @Test
    void testEquals() {
        assertTrue(user.equals(user));
        Object newUser = new User(netID, password, roles);
        assertTrue(user.equals(newUser));
        Object newUser2 = new User(netID, password, "TEACHER");
        assertFalse(user.equals(newUser2));
        Object newUser3 = new User("testUser2", password, roles);
        assertFalse(user.equals(newUser3));
        Object newUser4 = new User(netID, "password2", roles);
        assertFalse(user.equals(newUser4));
        assertFalse(user.equals(new Object()));
        assertNotEquals(user, null);
    }

    @Test
    void testHashCode() {
        assertNotNull(user.hashCode());
    }
}