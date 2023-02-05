package nl.tudelft.sem.template.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.CourseView;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import nl.tudelft.sem.template.validator.enrollment.CourseExistsValidator;
import nl.tudelft.sem.template.validator.enrollment.EnrolledValidator;
import nl.tudelft.sem.template.validator.enrollment.EnrollmentValidator;
import nl.tudelft.sem.template.validator.enrollment.MaxCapacityValidator;
import nl.tudelft.sem.template.validator.enrollment.NotEnrolledValidator;
import nl.tudelft.sem.template.validator.enrollment.OpenEnrollmentValidator;
import nl.tudelft.sem.template.validator.enrollment.RoleCheckValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping(path = "/enroll")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times for URLs
public class UnenrollmentController {

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
    public UnenrollmentController(CourseRepository courseRepository,
                                  StudentRepository studentRepository,
                                  TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }


    /**
     * Allows a teacher to remove a student from a course.
     * Checks that the currently authenticated user is a teacher
     * which gives said course.
     *
     * @param studentID  the netID of the student to remove
     * @param courseCode the code of the course
     */
    @DeleteMapping("/removeStudent/{studentID}/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(value = HttpStatus.OK)
    public void removeStudentFromCourse(@PathVariable String studentID,
                                        @PathVariable String courseCode,
                                        HttpServletRequest request) {

        EnrollmentValidator errHandler =
                new CourseExistsValidator(courseRepository)
                        .setNext(
                                new NotEnrolledValidator(studentRepository,
                                        teacherRepository)

                        );
        errHandler.handle(Integer.parseInt(courseCode), studentID, request);

        isTeacherOfCourse(courseCode);

        studentRepository.deleteByNetIDAndCourseCode(studentID, Integer.parseInt(courseCode));
    }

    private void isTeacherOfCourse(@PathVariable String courseCode) {

        String userNetID = UserInfoHelper.getNetID();

        if (teacherRepository.existsByNetIDAndCourseCode(userNetID,
                Integer.parseInt(courseCode)) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current "
                    + "authenticated user is not a teacher giving this course, "
                    + "and is therefore not authorized to remove users"
                    + "from the course");
        }
    }


    /**
     * Allows a teacher to remove a fellow teacher from a course.
     * Checks that the currently authenticated user is a teacher
     * which gives said course.
     *
     * @param teacherID  the netID of the teacher to remove
     * @param courseCode the code of the course
     */
    @DeleteMapping("/removeTeacher/{teacherID}/{courseCode}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(value = HttpStatus.OK)
    public void removeTeacherFromCourse(@PathVariable String teacherID,
                                        @PathVariable String courseCode,
                                        HttpServletRequest request) {

        EnrollmentValidator errHandler =
                new CourseExistsValidator(courseRepository)
                        .setNext(

                                new NotEnrolledValidator(studentRepository,
                                        teacherRepository)


                        );
        errHandler.handle(Integer.parseInt(courseCode), teacherID, request);

        isTeacherOfCourse(courseCode);

        teacherRepository.deleteByNetIDAndCourseCode(teacherID, Integer.parseInt(courseCode));
    }


    /**
     * Allows a student to unenroll from a course.
     * The netID is extracted from the current session.
     *
     * @param courseCode the code of the course
     */
    @DeleteMapping("/unenrollStudent/{courseCode}")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(value = HttpStatus.OK)
    public void unenrollStudent(@PathVariable String courseCode,
                                HttpServletRequest request) {

        String userNetID = UserInfoHelper.getNetID();

        EnrollmentValidator errHandler =
                new CourseExistsValidator(courseRepository)
                        .setNext(
                                new NotEnrolledValidator(studentRepository, teacherRepository)
                                        .setNext(new RoleCheckValidator()
                                        )
                        );
        errHandler.handle(Integer.parseInt(courseCode), userNetID, request);
        studentRepository.deleteByNetIDAndCourseCode(userNetID, Integer.parseInt(courseCode));

    }

}