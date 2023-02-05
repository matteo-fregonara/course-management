package nl.tudelft.sem.template.controllers;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import nl.tudelft.sem.template.validator.courses.ClearRoomValidator;
import nl.tudelft.sem.template.validator.courses.CourseValidator;
import nl.tudelft.sem.template.validator.courses.GetCompleteCourseContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(path = "/courses")
public class CourseDeleteController {

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
     * @param courseRepository Mocked instance of CourseRepository
     * @param studentRepository Mocked instance of StudentRepository
     * @param teacherRepository Mocked instance of TeacherRepository
     */
    public CourseDeleteController(CourseRepository courseRepository,
                            StudentRepository studentRepository,
                            TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    /** Endpoint to delete a course.
     *
     * @param courseCode code of the course to delete
     */
    @DeleteMapping("/delete/{courseCode}")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public void deleteCourse(@PathVariable String courseCode, HttpServletRequest request) {

        Course toBeRemoved = new Course();
        toBeRemoved.setCourseCode(Integer.parseInt(courseCode));

        CourseValidator errHandler = new GetCompleteCourseContents(courseRepository).setNext(
                // Clear the course-room association at the room service first
                new ClearRoomValidator()
        );

        errHandler.handle(toBeRemoved, request);

        studentRepository.deleteAllByCourseCode(courseCode);
        teacherRepository.deleteAllByCourseCode(courseCode);
        courseRepository.deleteCourse(courseCode);
    }
}
