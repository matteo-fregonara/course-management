package nl.tudelft.sem.template.controllers;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
public class UnenrollmentControllerTest {


    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    @Mock
    transient StudentRepository studentRepository;

    @Mock
    transient TeacherRepository teacherRepository;

    transient UnenrollmentController enrollmentController;

    transient WebClient.Builder webClientBuilder;

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


    @BeforeEach
    void setUp() {
        webClientBuilder = WebClient.builder();

        enrollmentController = new UnenrollmentController(courseRepository,
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

    }

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }




    @Test
    @WithMockUser("hasRole('TEACHER')")
    void removeStudentFromCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 2000)).willReturn(1);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(1);
        given(courseRepository.canEnroll(2000)).willReturn(true);

        Course c = new Course();
        c.setMaxSize(5);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(c));
        given(studentRepository.countAllByCourseCode(2000)).willReturn(1);

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    default:
                        return new MockResponse().setResponseCode(200).setBody("ROLE_STUDENT");
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("teacher1");
        given(UserInfoHelper.getNetID()).willReturn("teacher1");
        mockMvc.perform(MockMvcRequestBuilders.delete("/enroll/removeStudent/student3/2000"))
                .andExpect(status().isOk());
    }

    @Test
    void removeStudentInvalidCourseCode() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(false);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/removeStudent/student3/2000"))
                .andExpect(status().isConflict())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Course doesn't exist"));
        } else {
            fail("Response Error is null");
        }

    }

    @Test
    void removeStudentNotEnrolledInCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 2000)).willReturn(0);

        given(courseRepository.canEnroll(2000)).willReturn(true);

        Course c = new Course();
        c.setMaxSize(5);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(c));
        given(studentRepository.countAllByCourseCode(2000)).willReturn(1);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/removeStudent/student3/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                    .contains("Student not enrolled in course"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    void removeStudentUserNotEnrolledInCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("student3", 2000)).willReturn(1);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(0);

        given(courseRepository.canEnroll(2000)).willReturn(true);
        Course c = new Course();
        c.setMaxSize(5);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(c));
        given(studentRepository.countAllByCourseCode(2000)).willReturn(1);

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("teacher1");

        mockWebServer = new MockWebServer();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    default:
                        return new MockResponse().setResponseCode(200).setBody("ROLE_STUDENT");
                }
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/removeStudent/student3/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println("SearchTerm" + mvcResult.getResponse().getErrorMessage());

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Current "
                    + "authenticated user is not a teacher giving this course, "
                    + "and is therefore not authorized to remove users"
                    + "from the course"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    void removeTeacherFromCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(1);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher2", 2000)).willReturn(1);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("teacher2");

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


        mockMvc.perform(MockMvcRequestBuilders.delete("/enroll/removeTeacher/teacher1/2000"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTeacherInvalidCourseCode() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(false);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/removeTeacher/teacher1/2000"))
                .andExpect(status().isConflict())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Course doesn't exist"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    void removeTeacherNotEnrolledInCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(0);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/removeTeacher/teacher1/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage()
                    .contains("Teacher not enrolled in course"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    void removeTeacherUserNotEnrolledInCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher1", 2000)).willReturn(1);
        given(teacherRepository.existsByNetIDAndCourseCode("teacher2", 2000)).willReturn(0);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(openCourse));

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("teacher2");

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
                .delete("/enroll/removeTeacher/teacher1/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Current "
                    + "authenticated user is not a teacher giving this course, "
                    + "and is therefore not authorized to remove users"
                    + "from the course"));
        } else {
            fail("Response Error is null");
        }
    }


    @Test
    void unenrollStudent() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("enoritsyna", 2000)).willReturn(1);

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("enoritsyna");

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

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);

        mockMvc.perform(MockMvcRequestBuilders.delete("/enroll/unenrollStudent/2000"))
                .andExpect(status().isOk());
    }

    @Test
    void unenrollStudentInvalidCourseCode() throws Exception {
        given(courseRepository.existsById(6000)).willReturn(false);

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("enoritsyna");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/unenrollStudent/6000"))
                .andExpect(status().isConflict())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Course doesn't exist"));
        } else {
            fail("Response Error is null");
        }
    }

    @Test
    void unenrollStudentNotEnrolledInCourse() throws Exception {
        given(courseRepository.existsById(2000)).willReturn(true);
        given(studentRepository.existsByNetIDAndCourseCode("enoritsyna", 2000)).willReturn(0);

        UserInfo userInfo = mock(UserInfo.class);
        Authentication principal = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(principal);
        securityContextHolder.setContext(securityContext);
        given(securityContextHolder.getContext().getAuthentication().getPrincipal())
                .willReturn(userInfo);
        given(userInfo.getNetID()).willReturn("enoritsyna");

        Course c = new Course();
        c.setMaxSize(5);
        given(courseRepository.findCourseByCourseCode(2000)).willReturn(Optional.of(c));
        given(studentRepository.countAllByCourseCode(2000)).willReturn(1);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/enroll/unenrollStudent/2000"))
                .andExpect(status().isBadRequest())
                .andReturn();

        if (mvcResult.getResponse().getErrorMessage() != null) {

            System.out.println(mvcResult.getResponse().getErrorMessage());
            assertTrue(mvcResult.getResponse().getErrorMessage().contains("Student not"
                    + " enrolled in course"));
        } else {
            fail("Response Error is null");
        }
    }

}