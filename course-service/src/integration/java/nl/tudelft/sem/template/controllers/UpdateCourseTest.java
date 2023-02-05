package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
//Duplicate literals suppressed as identical strings were used multiple times in URLs
//Dataflow anomaly analysis suppressed as the lambda expression in the dispatcher
// is being mistakenly considered as null object
public class UpdateCourseTest {

    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    transient CourseUpdateController courseController;

    transient MockWebServer mockWebServer;

    transient String updateCourseMapping = "/courses/update";

    transient int courseCode;
    transient String description;
    transient int maxSize;
    transient boolean canEnroll;
    transient String canEnrollString;
    transient List<Student> student;
    transient List<Teacher> teacher;
    transient List<Course> list;
    transient Course course;

    transient JSONObject req;
    transient JSONObject req2;

    @BeforeEach
    void setUp() throws JSONException {
        courseController = new CourseUpdateController(courseRepository);

        courseCode = 1234;
        description = "desc";
        maxSize = 400;
        canEnroll = true;
        canEnrollString = "1";

        student = new ArrayList<>();
        Student student1 = new Student(0, "id", 1000);
        student.add(student1);
        teacher = new ArrayList<>();
        Teacher teacher1 = new Teacher(1, "net", 1000);
        teacher.add(teacher1);

        course =
            new Course(courseCode, maxSize, "ADS", description, canEnroll, 2345, student, teacher);

        req = new JSONObject();
        req.put("courseCode", 1234);
        req.put("maxSize", 400);
        req.put("description", "Do something awesome");
        req.put("canEnroll", false);
    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void invalidCourseCodeTest() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        given(courseRepository.checkCourse(courseCode)).willReturn(0);

        MvcResult mvcResult =
                mockMvc.perform(post(updateCourseMapping)
                        .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
                        .andExpect(status().isBadRequest()).andReturn();


        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                .contains("The specified course does not exist."));
        } else {
            throw new IllegalArgumentException("Response Error is null");
        }

    }

    @Test
    void invalidMaxSizeTest() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/checkFit/2345/400":
                        return new MockResponse().setResponseCode(200).setBody("false");
                    case "/rooms/maxSize/2345":
                        return new MockResponse().setResponseCode(200).setBody("350");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        given(courseRepository.checkCourse(courseCode)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(courseCode)).willReturn(Optional.of(course))
        ;

        MvcResult mvcResult = mockMvc.perform(post(updateCourseMapping)
                .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
                .andExpect(status().isBadRequest()).andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertEquals("Max students in course exceed room size: "
                    + "This room fits 350 people, but a course of 400 people is submitted.",
                mvcResult.getResponse().getErrorMessage());
        } else {
            throw new IllegalArgumentException("Response Error is null");
        }

    }

    @Test
    void invalidEnrollmentStatusTest() throws Exception {
        // test situations where enrollment status is neither 0 or 1

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/checkFit/2345/400":
                        return new MockResponse().setResponseCode(200).setBody("true");
                    case "/rooms/maxSize/2345":
                        return new MockResponse().setResponseCode(200).setBody("500");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        given(courseRepository.checkCourse(courseCode)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(courseCode)).willReturn(Optional.of(course));

        req2 = new JSONObject();
        req2.put("courseCode", 1234);
        req2.put("maxSize", 6);
        req2.put("description", "Do something awesome");
        req2.put("canEnroll", "bsgfewgwe");

        mockMvc.perform(post(updateCourseMapping)
                .contentType(MediaType.APPLICATION_JSON).content(req2.toString()))
                // use a non true or false value for canEnroll to generate an invalid case
                .andExpect(status().isBadRequest());
    }

    @Test
    void goodWeatherTest() throws Exception {
        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/rooms/checkFit/2345/400":
                        return new MockResponse().setResponseCode(200).setBody("true");
                    case "/rooms/maxSize/2345":
                        return new MockResponse().setResponseCode(200).setBody("500");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8083);

        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        given(courseRepository.checkCourse(courseCode)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(courseCode)).willReturn(Optional.of(course));

        mockMvc.perform(post(updateCourseMapping)
                .contentType(MediaType.APPLICATION_JSON).content(req.toString()))
                .andExpect(status().isOk()).andReturn();
        verify(courseRepository, atLeastOnce()).updateCourse(1234,
                "Do something awesome", 400, false);
    }

}
