package nl.tudelft.sem.template.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPrincipalTest {

    transient User user;
    transient UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "password", "STUDENT");
        userPrincipal = new UserPrincipal(user);
    }

    @Test
    void getUser() {
        assertEquals(user, userPrincipal.getUser());
    }

    @Test
    void setUser() {
        User user2 = new User("testUser2", "password2", "STUDENT");
        userPrincipal.setUser(user2);
        assertEquals(user2, userPrincipal.getUser());
    }

    @Test
    void getAuthorities() {
        assertNotNull(userPrincipal.getAuthorities());
    }

    @Test
    void getPassword() {
        assertEquals("password", userPrincipal.getPassword());
        assertEquals(null, new UserPrincipal(null).getPassword());
    }

    @Test
    void getUsername() {
        assertEquals("testUser", userPrincipal.getUsername());
    }

    @Test
    void getRoles() {
        assertEquals("STUDENT", userPrincipal.getRoles());
    }

    @Test
    void isAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }
}