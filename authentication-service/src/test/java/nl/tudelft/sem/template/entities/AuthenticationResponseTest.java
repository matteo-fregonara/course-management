package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthenticationResponseTest {

    @Test
    void getJwt() {
        AuthenticationResponse response = new AuthenticationResponse("jwt");
        assertEquals("jwt", response.getJwt());
    }
}