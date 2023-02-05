package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthenticationRequestTest {

    transient AuthenticationRequest request;
    transient AuthenticationRequest requestEmptyConstructor;

    @BeforeEach
    void setUp() {
        request = new AuthenticationRequest("testUser", "password");
        requestEmptyConstructor = new AuthenticationRequest();
    }

    @Test
    void constructorTest() {
        assertNotNull(request);
        assertNotNull(requestEmptyConstructor);
    }

    @Test
    void getPassword() {
        assertEquals("password", request.getPassword());
    }

    @Test
    void setPassword() {
        request.setPassword("pass");
        assertEquals("pass", request.getPassword());
    }

    @Test
    void getUsername() {
        assertEquals("testUser", request.getUsername());
    }

    @Test
    void setUsername() {
        request.setUsername("user");
        assertEquals("user", request.getUsername());
    }
}