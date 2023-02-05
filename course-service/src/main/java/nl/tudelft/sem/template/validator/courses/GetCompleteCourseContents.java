package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GetCompleteCourseContents extends BaseCourseValidator {

    private final transient CourseRepository courseRepository;

    @Autowired
    public GetCompleteCourseContents(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {

        course = courseRepository.findCourseByCourseCode(course.getCourseCode()).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "The specified course does not exist."
            )
        );

        super.checkNext(course, request);
    }

}
