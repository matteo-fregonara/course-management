package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserInfoTest {

    transient UserInfo userInfo;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfo("username", "roles");
    }

    @Test
    void getUsername() {
        assertEquals("username", userInfo.getNetID());
    }

    @Test
    void setUsername() {
        userInfo.setNetID("name");
        assertEquals("name", userInfo.getNetID());
    }

    @Test
    void getRoles() {
        assertEquals("roles", userInfo.getRoles());
    }

    @Test
    void setRoles() {
        userInfo.setRoles("test");
        assertEquals("test", userInfo.getRoles());
    }
}