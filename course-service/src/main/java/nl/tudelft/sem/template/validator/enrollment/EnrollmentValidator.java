package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

public interface EnrollmentValidator {

    EnrollmentValidator setNext(EnrollmentValidator handler);

    void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException;

}
