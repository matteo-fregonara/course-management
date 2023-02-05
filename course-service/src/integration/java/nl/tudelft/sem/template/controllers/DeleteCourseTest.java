package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;

@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
//Duplicate literals suppressed as identical strings were used multiple times in URLs
//Dataflow anomaly analysis suppressed as the lamda expression in the dispatcher
// is being mistakenly considered as null object
@SpringBootTest
@AutoConfigureMockMvc
public class DeleteCourseTest {

    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    @Mock
    transient StudentRepository studentRepository;

    @Mock
    transient TeacherRepository teacherRepository;

    transient CourseDeleteController courseController;

    transient MockWebServer mockWebServer;

    transient String deleteCourseMapping = "/courses/delete/153820";

    @BeforeEach
    void setUp() {
        courseController = new CourseDeleteController(courseRepository,
            studentRepository, teacherRepository);
    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void courseNotExistedAtCourseServiceCheck() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        // 153820 is the mocked course which is supposed to be not existed
        when(courseRepository.checkCourse(153820)).thenReturn(0);

        MvcResult mvcResult = mockMvc.perform(delete(deleteCourseMapping))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("specified course does"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    // This test checks the situation where data are out of sync between room and course services
    void courseNotExistedAtRoomServiceCheck() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        // 3456 is the mocked course which exists locally but not at Room service
        when(courseRepository.checkCourse(3456)).thenReturn(1);

        // 3456 exists at Course but let's mock a room service response where 3456 is not at room.
        MvcResult mvcResult = mockMvc.perform(delete("/courses/delete/3456"))
            .andExpect(status().isBadRequest())
            .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("specified course does"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    void goodWeatherTest() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/clearRoom/12":
                        return new MockResponse().setResponseCode(200);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        Course dummy = new Course();
        dummy.setRoomCode(12);
        // 3456 is the mocked course which exists
        when(courseRepository.findCourseByCourseCode(3456)).thenReturn(Optional.of(dummy));

        // 3456 exists at Course but let's mock a room service response where 3456 is not at room.
        mockMvc.perform(delete("/courses/delete/3456"))
            .andExpect(status().isOk())
            .andReturn();

        dummy.setRoomCode(13);
        when(courseRepository.findCourseByCourseCode(3456)).thenReturn(Optional.of(dummy));
        mockMvc.perform(delete("/courses/delete/3456"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}

