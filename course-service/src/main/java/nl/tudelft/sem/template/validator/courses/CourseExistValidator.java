package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CourseExistValidator extends BaseCourseValidator {

    private final transient CourseRepository courseRepository;

    @Autowired
    public CourseExistValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {
        try {
            int courseCode = course.getCourseCode();
            preExistingCourseCheck(courseCode);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(course, request);
    }

    private void preExistingCourseCheck(int courseCode) throws ResponseStatusException {
        if (courseRepository.findCourseByCourseCode(courseCode).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course already exists");
        }
    }

}
