package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.web.server.ResponseStatusException;

public interface CourseValidator {

    CourseValidator setNext(CourseValidator handler);

    void handle(Course course, HttpServletRequest request) throws ResponseStatusException;

}
