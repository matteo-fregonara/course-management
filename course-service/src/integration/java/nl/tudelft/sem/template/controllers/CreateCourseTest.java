package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
//Duplicate literals suppressed as identical strings were used multiple times in URLs
//Dataflow anomaly analysis suppressed as the lamda expression in the dispatcher
// is being mistakenly considered as null object
@SpringBootTest
@AutoConfigureMockMvc
public class CreateCourseTest {

    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    @Mock

    transient CourseCreateController courseController;

    transient MockWebServer mockWebServer;

    transient JSONObject req;

    transient String createCourseMapping = "/courses/createCourse";

    @BeforeEach
    void setUp() throws JSONException {
        courseController = new CourseCreateController(courseRepository);

        req = new JSONObject();
        req.put("courseCode", 1234);
        req.put("maxSize", 6);
        req.put("courseName", "Group Session");
        req.put("description", "Do something awesome");
        req.put("canEnroll", false);
        req.put("roomCode", 12);
    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void invalidFieldCheck() throws Exception {
        req.remove("roomCode");

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        MvcResult mvcResult = mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Validation failed"));
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void duplicateTest() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/maxSize/12":
                        return new MockResponse().setResponseCode(200).setBody("144");
                    case "/rooms/available/12":
                        return new MockResponse().setResponseCode(200).setBody("true");
                    case "/rooms/bookRoom/12/3456":
                        return new MockResponse().setResponseCode(200);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        Course courseFound = new Course();
        Optional<Course> courseOptional = Optional.of(courseFound);
        when(courseRepository.findCourseByCourseCode(3456)).thenReturn(courseOptional);

        req.remove("courseCode");
        req.put("courseCode", 3456);

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void roomOccupiedCheck() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/maxSize/12":
                        return new MockResponse().setResponseCode(200).setBody("144");
                    case "/rooms/available/12":
                        return new MockResponse().setResponseCode(200).setBody("false");
                    case "/rooms/bookRoom/12/1234":
                        return new MockResponse().setResponseCode(409);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        MvcResult mvcResult = mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Room not available"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void roomNoFitCheck() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/maxSize/12":
                        return new MockResponse().setResponseCode(200).setBody("1");
                    case "/rooms/available/12":
                        return new MockResponse().setResponseCode(200).setBody("true");
                    case "/rooms/bookRoom/12/1234":
                        return new MockResponse().setResponseCode(200);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        MvcResult mvcResult = mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("exceed room size"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void roomNoExistCheck() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/maxSize/12":
                        return new MockResponse().setResponseCode(200).setBody("144");
                    case "/rooms/available/12":
                        return new MockResponse().setResponseCode(404);
                    case "/rooms/bookRoom/12/1234":
                        return new MockResponse().setResponseCode(404);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        MvcResult mvcResult = mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("No such room"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void lastMinuteNotAvailableCheck() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/maxSize/12":
                        return new MockResponse().setResponseCode(200).setBody("144");
                    case "/rooms/available/12":
                        return new MockResponse().setResponseCode(200).setBody("true");
                    case "/rooms/bookRoom/12/1234":
                        return new MockResponse().setResponseCode(409);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        Optional<Course> courseOptional = Optional.empty();
        when(courseRepository.findCourseByCourseCode(3456)).thenReturn(courseOptional);

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        MvcResult mvcResult = mockMvc.perform(post(createCourseMapping)
            .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertEquals("Room not available", mvcResult.getResponse().getErrorMessage());
        } else {
            fail("Response Error is null");
        }
    }
}
