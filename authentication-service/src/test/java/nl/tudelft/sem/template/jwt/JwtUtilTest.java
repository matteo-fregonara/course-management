package nl.tudelft.sem.template.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.entities.UserPrincipal;
import nl.tudelft.sem.template.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

    private transient JwtUtil jwtUtil;
    private transient String jwtToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        UserPrincipal user = new UserPrincipal(
            new User("net id", "pass", "ROLE_TEACHER")
        );

        jwtToken = jwtUtil.generateToken(user);
    }

    @Test
    void extractUsernameTest() {
        assertEquals("net id", jwtUtil.extractUsername(jwtToken));
    }

    @Test
    void extractExpirationTest() {
        assertEquals(jwtUtil.extractClaim(jwtToken, Claims::getExpiration),
            jwtUtil.extractExpiration(jwtToken));
    }

    @Test
    void validateWrongUserTest() {
        assertFalse(jwtUtil.validateToken(jwtToken, "wrong user"));
    }

    @Test
    void validateRightUserTest() {
        assertTrue(jwtUtil.validateToken(jwtToken, "net id"));
    }

    @Test
    void validateNullUserTest() {
        assertFalse(jwtUtil.validateToken(jwtToken, null));
    }
}
