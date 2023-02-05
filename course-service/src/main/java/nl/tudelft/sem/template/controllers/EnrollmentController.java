package nl.tudelft.sem.template.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.CourseView;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import nl.tudelft.sem.template.validator.enrollment.EnrollmentValidator;
import nl.tudelft.sem.template.validator.enrollment.EnrollmentValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(path = "/enroll")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times for URLs
public class EnrollmentController {

    @Autowired
    private transient CourseRepository courseRepository;

    @Autowired
    private transient StudentRepository studentRepository;

    @Autowired
    private transient TeacherRepository teacherRepository;

    /**
     * Constructor that allow mocked instances of the class attributes to pass through.
     * This is an alternative to the `@InjectMock` annotation.
     *
     * @param courseRepository  Mocked instance of CourseRepository
     * @param studentRepository Mocked instance of StudentRepository
     * @param teacherRepository Mocked instance of TeacherRepository
     */
    public EnrollmentController(CourseRepository courseRepository,
                                StudentRepository studentRepository,
                                TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("") // Shows all courses that are open for enrollment at the moment
    public @ResponseBody
    List<CourseView> getCoursesOpenForEnrollment() {
        return courseRepository.findCourseOpenEnrol();
    }


    /**
     * Allows a student to enroll themselves in a course.
     *
     * @param courseCode the course they want to enroll in
     */
    @PostMapping("/{courseCode}")
    @ResponseStatus(value = HttpStatus.OK)
    public void enrollInCourse(@PathVariable String courseCode, HttpServletRequest request) {
        String netID = UserInfoHelper.getNetID();

        enrollCourse(courseCode, netID, request);
    }


    /**
     * Allows teachers to enroll students into a course.
     *
     * @param courseCode to enroll the student to
     * @param studentID  studentID to enroll
     */
    @PostMapping("/{studentID}/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(value = HttpStatus.OK)
    public void enrollCourse(@PathVariable String courseCode,
                             @PathVariable String studentID, HttpServletRequest request) {

        EnrollmentValidator errHandler = EnrollmentValidatorFactory.getEnrollmentValidator(
            "student", courseRepository, studentRepository, teacherRepository);

        errHandler.handle(Integer.parseInt(courseCode), studentID, request);

        studentRepository.save(new Student(studentID, Integer.parseInt(courseCode)));

    }


    /**
     * Allows for a teacher to add themselves or another teacher to a course.
     *
     * @param courseCode the code of the course
     * @param teacherID  netID of the teacher to add
     */
    @PostMapping("/addTeacher/{teacherID}/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(value = HttpStatus.OK)
    public void addTeacherToCourse(@PathVariable String courseCode,
                                   @PathVariable String teacherID,
                                   HttpServletRequest request) {

        EnrollmentValidator errHandler = EnrollmentValidatorFactory.getEnrollmentValidator(
            "teacher", courseRepository, studentRepository, teacherRepository);

        errHandler.handle(Integer.parseInt(courseCode), teacherID, request);
        teacherRepository.save(new Teacher(teacherID, Integer.parseInt(courseCode)));

    }


}

