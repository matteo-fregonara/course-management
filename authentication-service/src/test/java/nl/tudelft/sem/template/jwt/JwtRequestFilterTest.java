package nl.tudelft.sem.template.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.entities.UserPrincipal;
import nl.tudelft.sem.template.security.JwtRequestFilter;
import nl.tudelft.sem.template.security.JwtUtil;
import nl.tudelft.sem.template.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times in headers
public class JwtRequestFilterTest {

    @Mock
    private transient UserService userDetailsService;

    @Mock
    private transient JwtUtil jwtUtil;

    private transient String jwtHeader;
    private transient String jwtToken;
    private transient MockHttpServletResponse response;
    private transient MockHttpServletRequest request;
    private transient FilterChain filterChain;
    private transient SecurityContextImpl nullAuth;

    @InjectMocks
    private transient JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = (filterRequest, filterResponse) -> { };

        jwtToken = "randomusername";
        jwtHeader = "Bearer " + jwtToken;

        nullAuth = new SecurityContextImpl();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void nullHeaderTest() {

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void wrongJwtFormatTest() {

        request.addHeader("Authorization", "WRONG TOKEN FORMAT");

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void nullUserTest() {

        given(jwtUtil.extractUsername(jwtToken)).willReturn(null);

        request.addHeader("Authorization", jwtHeader);

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void userNotInDatabaseTest() {
        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(userDetailsService.loadUserByUsername("username")).willThrow(
                new UsernameNotFoundException("User not found")
        );

        request.addHeader("Authorization", jwtHeader);

        assertThrows(UsernameNotFoundException.class, () -> {
            jwtRequestFilter.doFilter(request, response, filterChain);
        });

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void nullUserNameTest() {
        given(jwtUtil.extractUsername(jwtToken)).willReturn(null);

        request.addHeader("Authorization", jwtHeader);
        request.addHeader("Authorization", "WRONG TOKEN FORMAT");

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void invalidJwtTokenTest() {
        UserDetails userDetails = new UserPrincipal(
            new User("username", "password", "ROLE_TEACHER")
        );

        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(userDetailsService.loadUserByUsername("username"))
            .willReturn(userDetails);
        given(jwtUtil.validateToken(jwtToken, "username"))
            .willReturn(false);

        request.addHeader("Authorization", jwtHeader);

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        assertEquals(nullAuth, SecurityContextHolder.getContext());
    }

    @Test
    void validJwtTokenTest() {
        UserDetails userDetails = new UserPrincipal(
            new User("username", "password", "ROLE_TEACHER")
        );

        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(userDetailsService.loadUserByUsername("username")).willReturn(userDetails);
        given(jwtUtil.validateToken(jwtToken, "username"))
            .willReturn(true);

        request.addHeader("Authorization", jwtHeader);

        try {
            jwtRequestFilter.doFilter(request, response, filterChain);

        } catch (IOException | ServletException ex) {
            fail();
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        assertEquals(usernamePasswordAuthenticationToken,
            SecurityContextHolder.getContext().getAuthentication());
    }
}
