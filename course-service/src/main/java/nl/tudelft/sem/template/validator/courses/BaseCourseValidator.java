package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseCourseValidator implements CourseValidator {

    private transient CourseValidator next;
    protected final transient String roomServiceUri = "http://localhost:8083/rooms";

    /**
     * Set the next validator in the validation chain. Chain executes until a next that is `null`.
     *
     * @param v the next validator in the chain, null
     * @return validator after setting the next validator
     */
    public CourseValidator setNext(CourseValidator v) {
        this.next = v;

        return this;
    }

    protected void checkNext(Course course, HttpServletRequest request)
        throws ResponseStatusException {
        if (next == null) {
            return;
        }

        next.handle(course, request);
    }

}