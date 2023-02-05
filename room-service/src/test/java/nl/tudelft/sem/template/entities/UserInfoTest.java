package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserInfoTest {
    transient UserInfo userInfoDefault;

    @BeforeEach
    void setUp() {
        userInfoDefault = new UserInfo("netid", "ROLE");
    }

    @Test
    void constructorEmptyTest() {
        UserInfo emptyConstructor = new UserInfo();
        assertNotNull(emptyConstructor);
    }

    @Test
    void constructorFilledTest() {
        assertNotNull(userInfoDefault);
    }

    @Test
    void getNetID() {
        assertEquals("netid", userInfoDefault.getNetID());
    }

    @Test
    void setNetID() {
        userInfoDefault.setNetID("new netID");
        assertEquals("new netID", userInfoDefault.getNetID());
    }

    @Test
    void getRole() {
        assertEquals("ROLE", userInfoDefault.getRoles());
    }

    @Test
    void setRole() {
        userInfoDefault.setRoles("NEW ROLE");
        assertEquals("NEW ROLE", userInfoDefault.getRoles());
    }


}
