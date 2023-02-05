package nl.tudelft.sem.template.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import javax.servlet.FilterChain;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;


@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
//Dataflow anomaly analysis suppressed as the lamda expression in the dispatcher
// is being mistakenly considered as null object
public class JwtRequestFilterTest {
    transient MockWebServer mockWebServer;

    transient String userInfo;

    @BeforeEach
    void setUp() {
        userInfo = "{ "
            + "\"netID\": \"dummy\","
            + "\"roles\": \"ROLE_STUDENT\""
            + " }";
    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void validJWT() throws Exception {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/validate")) {
                    return new MockResponse().setResponseCode(200).setBody("true");
                } else if (request.getPath().equals("/getUserInfo")) {
                    return new MockResponse().setResponseCode(200).setBody(userInfo)
                        .setHeader("Content-type", "application/json");
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081); //Auth port


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwttoken");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (filterRequest, filterResponse) -> { };

        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
    }

    @Test
    void invalidJWT() throws Exception {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/validate")) {
                    return new MockResponse().setResponseCode(200).setBody("false");
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081); //Auth port


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authentication", "Bearer jwttoken");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (filterRequest, filterResponse) -> { };

        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();

        assertThrows(ResponseStatusException.class,
            () -> {
                jwtRequestFilter.doFilterInternal(request, response, filterChain);
            });


    }

    @Test
    void exceptionInvalidJWT() throws Exception {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/validate")) {
                    return new MockResponse().setResponseCode(403);
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081); //Auth port


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authentication", "Bearer jwttoken");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (filterRequest, filterResponse) -> { };

        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();

        assertThrows(ResponseStatusException.class,
            () -> {
                jwtRequestFilter.doFilterInternal(request, response, filterChain);
            });

    }

}
