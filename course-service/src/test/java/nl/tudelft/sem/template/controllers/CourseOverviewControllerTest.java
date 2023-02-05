package nl.tudelft.sem.template.controllers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class CourseOverviewControllerTest {

    @Autowired
    transient MockMvc mockMvc;

    @Mock
    transient CourseRepository courseRepository;

    @Mock
    transient StudentRepository studentRepository;

    @Mock
    transient TeacherRepository teacherRepository;

    @InjectMocks
    transient CourseOverviewController courseOverviewController;

    transient Course course;
    transient List<Student> student;
    transient List<Teacher> teacher;
    transient List<Course> list;
    transient List courseCode;
    transient String result;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseOverviewController).build();
        student = new ArrayList<>();
        Student student1 = new Student(0, "id", 1000);
        student.add(student1);
        teacher = new ArrayList<>();
        Teacher teacher1 = new Teacher(1, "net", 1000);
        teacher.add(teacher1);
        course = new Course(1000, 200, "Course", "Description", false, 1101, student, teacher);
        list = new ArrayList<>();
        list.add(course);
        courseCode = new ArrayList();
        courseCode.add(1000);
        result = "[{\"courseCode\":1000,\"maxSize\":200,\"courseName\""
                + ":\"Course\",\"description\":\"Description\""
                + ",\"canEnroll\":false,\"roomCode\":1101,\"student\":"
                + "[{\"index\":0,\"netID\":\"id\",\"courseCode\":1000}],"
                + "\"teacher\":[{\"index\":1,\"netID\":\"net\","
                + "\"courseCode\":1000}]}]";
    }

    @Test
    void getAllStudents() throws Exception {
        given(studentRepository.findAll()).willReturn(student);
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/getAllStudents")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "[{\"index\":0,\"netID\":\"id\",\"courseCode\":1000}]"));
    }

    @Test
    void getCoursesInvolvedInStudent() throws Exception {
        given(studentRepository.findByNetID("id")).willReturn(courseCode);
        given(courseRepository.findCourseByCourseCodes(courseCode)).willReturn(list);
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/student/getCourses/id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void getCoursesInvolvedInTeacher() throws Exception {
        given(teacherRepository.findByNetID("net")).willReturn(courseCode);
        given(courseRepository.findCourseByCourseCodes(courseCode)).willReturn(list);
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/teacher/getCourses/net")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
    }

    @Test
    void getAllCoursesTeacher() throws Exception {
        given(courseRepository.findAll()).willReturn(list);
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/teacher/getAll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
        verify(courseRepository).findAll();
    }

    @Test
    void getAllCoursesSortedTeacher() throws Exception {
        given(courseRepository.getAllSorted()).willReturn(list);
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/teacher/getAllSorted")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
        verify(courseRepository).getAllSorted();
    }

    @Test
    void getCourseByCourseCode() throws Exception {
        given(courseRepository.findCourseByCourseCode(1000)).willReturn(Optional.of(course));
        mockMvc.perform(MockMvcRequestBuilders.get("/courses/1000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"courseCode\":1000,\"maxSize\":200,\"courseName\""
                                + ":\"Course\",\"description\":\"Description\""
                                + ",\"canEnroll\":false,\"roomCode\":1101,\"student\":"
                                + "[{\"index\":0,\"netID\":\"id\",\"courseCode\":1000}],"
                                + "\"teacher\":[{\"index\":1,\"netID\":\"net\","
                                + "\"courseCode\":1000}]}"));
    }
}