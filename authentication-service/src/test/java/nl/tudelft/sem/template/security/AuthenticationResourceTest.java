package nl.tudelft.sem.template.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.entities.UserInfo;
import nl.tudelft.sem.template.entities.UserPrincipal;
import nl.tudelft.sem.template.repositories.UserRepository;
import nl.tudelft.sem.template.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times in headers
class AuthenticationResourceTest {

    @Autowired
    transient MockMvc mockMvc;

    @Mock
    private transient UserService userDetailsService;

    @Mock
    private transient JwtUtil jwtUtil;

    @Mock
    private transient UserRepository userRepository;

    @InjectMocks
    private transient AuthenticationResource authenticationResource;

    private transient String jwtHeader;
    private transient String jwtToken;
    private transient MockHttpServletResponse response;
    private transient MockHttpServletRequest request;

    transient User student;
    transient User teacher;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        jwtToken = "randomUsername";
        jwtHeader = "Bearer " + jwtToken;
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationResource).build();

    }

    @Test
    void validateUserHeaderNull() {
        assertThrows(ResponseStatusException.class, () -> {
            authenticationResource.validateUser(request, response);
        });
    }

    @Test
    void validateUserHeaderWrong() {
        request.addHeader("Authorization", "Wrong");
        assertThrows(ResponseStatusException.class, () -> {
            authenticationResource.validateUser(request, response);
        });
    }

    @Test
    void validateUserValidateTokenNull() {
        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(jwtUtil.validateToken(jwtToken, "username")).willReturn(false);
        request.addHeader("Authorization", jwtHeader);
        assertFalse(authenticationResource.validateUser(request, response));
    }

    @Test
    void validateUserUserNull() {
        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(jwtUtil.validateToken(jwtToken, "username")).willReturn(true);
        given(userDetailsService.loadUserByUsername("username")).willThrow(
                            new UsernameNotFoundException("JWT user doesnt exist"));
        request.addHeader("Authorization", jwtHeader);
        assertThrows(ResponseStatusException.class, () -> {
            authenticationResource.validateUser(request, response);
        });
    }

    @Test
    void validateUser() {
        UserDetails userDetails = new UserPrincipal(
                new User("username", "password", "ROLE_TEACHER"));

        given(jwtUtil.extractUsername(jwtToken)).willReturn("username");
        given(jwtUtil.validateToken(jwtToken, "username")).willReturn(true);
        given(userDetailsService.loadUserByUsername("username")).willReturn(userDetails);
        request.addHeader("Authorization", jwtHeader);
        assertTrue(authenticationResource.validateUser(request, response));
    }

    @Test
    void getUserInfoValidateFalse() {
        request.addHeader("Authorization", jwtHeader);
        given(authenticationResource.validateUser(request, response)).willReturn(false);
        assertThrows(ResponseStatusException.class, () -> {
            authenticationResource.getUserInfo(request, response);
        });
    }

    @Test
    void getUserInfo() {
        request.addHeader("Authorization", jwtHeader);
        UserPrincipal userPrincipal = new UserPrincipal(
                new User("username", "password", "ROLE_TEACHER"));
        given(authenticationResource.validateUser(request, response)).willReturn(true);
        given(userDetailsService.loadUserByUsername(
                jwtUtil.extractUsername(jwtToken))).willReturn(userPrincipal);

        assertEquals(new UserInfo(userPrincipal.getUsername(), userPrincipal.getRoles()).getNetID(),
                authenticationResource.getUserInfo(request, response).getNetID());
        assertEquals(new UserInfo(userPrincipal.getUsername(), userPrincipal.getRoles()).getRoles(),
                authenticationResource.getUserInfo(request, response).getRoles());
    }

    @Test
    void getRoleByIDStudent() throws Exception {
        given(userRepository.existsById("student1")).willReturn(true);
        given(userRepository.findRoleByUsername("student1")).willReturn("ROLE_STUDENT");
        mockMvc.perform(MockMvcRequestBuilders.get("/getRole/student1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ROLE_STUDENT"));
    }

    @Test
    void getRoleByIDTeacher() throws Exception {
        given(userRepository.existsById("teacher1")).willReturn(true);
        given(userRepository.findRoleByUsername("teacher1")).willReturn("ROLE_TEACHER");
        mockMvc.perform(MockMvcRequestBuilders.get("/getRole/teacher1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ROLE_TEACHER"));
    }

    @Test
    void getRoleByIDNonExistent() throws Exception {
        given(userRepository.existsById("student222")).willReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/getRole/student222"))
                .andExpect(status().isOk())
                .andExpect(content().string("Doesn't Exist"));
    }


}