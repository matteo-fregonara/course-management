package nl.tudelft.sem.template.controllers;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.CourseUpdateRequestModel;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.validator.courses.CanEnrollStatusNumberValidator;
import nl.tudelft.sem.template.validator.courses.CourseValidator;
import nl.tudelft.sem.template.validator.courses.RoomFitValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping(path = "/courses")
public class CourseUpdateController {

    @Autowired
    private transient CourseRepository courseRepository;

    /**
     * Constructor that allow mocked instances of the class attributes to pass through.
     * This is an alternative to the `@InjectMock` annotation.
     *
     * @param courseRepository Mocked instance of CourseRepository
     */
    public CourseUpdateController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /** Endpoint to update a course.
     *
     * @param updatedCourse A valid request body of type CourseUpdateRequestModel
     *                      needs to be provided, containing a course code, max
     *                      size, description and enrollment status
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void updateCourse(@Valid @RequestBody CourseUpdateRequestModel updatedCourse,
                             HttpServletRequest request) {

        // Checks that the updated maxSize is smaller or equal to the room size
        Optional<Course> optionalCourse = courseRepository.findCourseByCourseCode(
                updatedCourse.getCourseCode());

        // This check is not part of the validator for the following reasons:
        // orElseThrow is a two birds with one stone method
        // Validation chain requires an instance of course to function
        Course course = optionalCourse.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "The specified course does not exist.")
        );

        // Change the maxSize to the proposed maxSize so the validator can validate the new value
        course.setMaxSize(updatedCourse.getMaxSize());

        CourseValidator errHandler = new RoomFitValidator().setNext(
                new CanEnrollStatusNumberValidator()
        );

        errHandler.handle(course, request);

        courseRepository.updateCourse(updatedCourse.getCourseCode(), updatedCourse.getDescription(),
                updatedCourse.getMaxSize(), updatedCourse.isCanEnroll());
    }
}
