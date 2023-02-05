package nl.tudelft.sem.template.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.CourseView;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import nl.tudelft.sem.template.entities.UserInfo;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

/**
 * <p>
 * DataflowAnomalyAnalysis is suppressed due to it has false positive near dispatchers.
 * </p>
 *
 * <p>
 * AvoidDuplicateLiterals because I wanted to change the uris in dispatcher without affecting other
 * tests.
 * </p>
 */

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
//Dataflow AnomalyAnalysis is suppressed due to it has false positive near dispatchers.
//AvoidDuplicateLiterals because I wanted to change the uris in dispatcher
// without affecting other tests.
public class EnrollControllerTest {


    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    @Mock
    transient StudentRepository studentRepository;

    @Mock
    transient TeacherRepository teacherRepository;

    transient EnrollmentController enrollmentController;


    transient MockWebServer mockWebServer;

    transient SecurityContextHolder securityContextHolder;

    transient Course course;
    transient Course courseMax;
    transient Course openCourse;
    transient CourseView openCourseView;
    transient List<Student> student;
    transient List<Teacher> teacher;
    transient List<Course> list;
    transient List courseCode;
    transient String v2;
    transient String openForEnrollmentCourseView;


    @BeforeEach
    void setUp() {

        enrollmentController = new EnrollmentController(courseRepository,
                studentRepository, teacherRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
        student = new ArrayList<>();
        Student student1 = new Student(0, "student1", 1000);
        student.add(student1);
        teacher = new ArrayList<>();
        Teacher teacher1 = new Teacher(1, "teacher1", 1000);
        teacher.add(teacher1);
        openCourse = new Course(2000, 200, "CourseOpenForEnroll",
                "DescriptionOfCourse", true, 1010, null, teacher);
        course = new Course(1000, 2, "Course", "Description",
                false, 1101, student, teacher);
        courseMax = new Course(1110, 1, "Course", "Description",
                false, 1101, student, teacher);
        list = new ArrayList<>();
        list.add(course);
        list.add(openCourse);
        courseCode = new ArrayList();
        courseCode.add(1000);
        openCourseView = new CourseView() {
            @Override
            public String getUser_friendly_name() {
                return openCourse.getCourseName();
            }

            @Override
            public Integer getCourse_code() {
                return openCourse.getCourseCode();
            }

            @Override
            public String getDescription() {
                return openCourse.getDescription();
            }

            @Override
            public Integer getRoom_code() {
                return openCourse.getRoomCode();
            }
        };

        openForEnrollmentCourseView = "[{\"description\":\"DescriptionOfCourse\","
                + "\"course_code\":2000,\"room_code\":1010,"
                + "\"user_friendly_name\":\"CourseOpenForEnroll\"}]";

        v2 = "[{\"description\":\"DescriptionOfCourse\",\"user_friendly_name\""
                + ":\"CourseOpenForEnroll\",\"course_code\":2000,\"room_code\":1010}]";

    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void getCoursesOpenForEnrollment() throws Exception {
        List<CourseView> courseViewList = new ArrayList<>();
        courseViewList.add(openCourseView);
        given(courseRepository.findCourseOpenEnrol()).willReturn(courseViewList);
        mockMvc.perform(MockMvcRequestBuilders.get("/enroll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void enrollInCourse() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));
        given(courseRepository.canEnroll(2000)).willReturn(true);
        given(studentRepository.countAllByCourseCode(2000)).willReturn(2);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 2000)).willReturn(0);

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/student3":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_STUDENT");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student3/2000"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser("hasRole('TEACHER')")
    void enrollCourse() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));
        given(courseRepository.canEnroll(2000)).willReturn(true);
        given(studentRepository.countAllByCourseCode(2000)).willReturn(2);
        given(studentRepository.existsByNetIDAndCourseCode("enoritsyna", 2000)).willReturn(0);

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/enoritsyna":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_STUDENT");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
            .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("enoritsyna");

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/2000"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void enrollCourseMaxCapacity() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));
        given(studentRepository.countAllByCourseCode(2000)).willReturn(200);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 2000)).willReturn(0);
        String urlTemp = "/enroll/student2/2000";
        when(mockMvc.perform(MockMvcRequestBuilders.post(urlTemp))).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Course has reached maximum number of students"));
        mockMvc.perform(MockMvcRequestBuilders.post(urlTemp))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Course has reached maximum number of students"));
    }

    @Test
    void enrollCourseMaxCapacityCoverage() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(courseMax);
        given(courseRepository.existsById(1110)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(1110)).willReturn(Optional.of(courseMax));
        given(studentRepository.countAllByCourseCode(1110)).willReturn(1);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 1110)).willReturn(0);

        String urlTemp = "/enroll/student3/1110";
        when(mockMvc.perform(MockMvcRequestBuilders.post(urlTemp))).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Course has reached maximum number of students"));
        mockMvc.perform(MockMvcRequestBuilders.post(urlTemp))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Course has reached maximum number of students"));
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void enrollCourseNonExistent() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(4000)).willReturn(false);
        when(mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student2/4000")))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found"));
        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student2/4000"))
                .andExpect(status().isBadRequest()).andExpect(status().reason("Course not found"));
    }

    @Test
    void enrollCourseAlreadyEnrolled() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);
        given(courseRepository.existsById(1000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("student1", 1000)).willReturn(1);

        given(courseRepository.findCourseByCourseCode(1000)).willReturn(Optional.of(course));
        given(courseRepository.canEnroll(1000)).willReturn(true);


        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student1/1000"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("User already enrolled in this course"));
    }

    @Test
    void addTeacherAsStudent() throws Exception {
        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));
        given(courseRepository.canEnroll(2000)).willReturn(true);
        given(studentRepository.countAllByCourseCode(2000)).willReturn(2);
        given(studentRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(0);


        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/teacher1":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_TEACHER");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/enroll/teacher1/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                    .contains("Specified user is not a student or does not exist"));
        } else {
            fail("Response Error is null");
        }
    }


    @Test
    void maxNumOfStudents() throws Exception {

        List<Course> courseList = new ArrayList<>();
        courseList.add(openCourse);
        given(courseRepository.existsById(1110)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(1110)).willReturn(Optional.of(courseMax));
        given(courseRepository.canEnroll(1110)).willReturn(true);
        given(studentRepository.countAllByCourseCode(1110)).willReturn(1);
        given(studentRepository.existsByNetIDAndCourseCode("student333", 1110)).willReturn(0);

        when(mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student333/1110")))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Course has reached maximum number of students"));
        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/student3/1110"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Course has reached maximum number of students"));

    }





    @Test
    void maxCapacityCheck() throws Exception {
        given(courseRepository.findCourseByCourseCode(1000)).willReturn(Optional.of(course));
        given(studentRepository.countAllByCourseCode(1000)).willReturn(1);
        course.getStudent().add(new Student(1, "student4", 1000));
        given(studentRepository.countAllByCourseCode(1000)).willReturn(2);

        mockMvc.perform(MockMvcRequestBuilders.get("/enroll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    @WithMockUser("hasRole('TEACHER')")
    void addTeacherToCourse() throws Exception {

        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/teacher1":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_TEACHER");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/addTeacher/teacher1/2000"))
                .andExpect(status().isOk());
    }

    @Test
    void addStudentAsTeacher() throws Exception {

        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.existsById(1000)).willReturn(true);
        // given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 1000)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/student1":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_STUDENT");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/enroll/addTeacher/student1/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                    .contains("Specified user is not a teacher or does not exist"));
        } else {
            fail("Response Error is null");
        }
    }



    @Test
    void teacherNullForRoleCheck() throws Exception {

        given(courseRepository.existsById(2000)).willReturn(true);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/teacher123":
                        return new MockResponse().setResponseCode(404);
                    default:
                        return new MockResponse().setResponseCode(400);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/enroll/addTeacher/teacher123/2000"))
                .andExpect(status().isNotFound())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("No such user"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void addTeacherToNonExistentCourse() throws Exception {

        given(courseRepository.existsById(3000)).willReturn(false);

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/getRole/teacher1":
                        return new MockResponse().setResponseCode(200).setBody("ROLE_TEACHER");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        mockMvc.perform(MockMvcRequestBuilders.post("/enroll/addTeacher/teacher1/2000"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void addExistingTeacherCourse() throws Exception {


        given(courseRepository.existsById(1000)).willReturn(true);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 1000)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(1000)).willReturn(Optional.of(course));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/enroll/addTeacher/teacher1/1000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                    .contains("User already enrolled in this course"));
        } else {
            fail("Response Error is null");
        }

    }


}



