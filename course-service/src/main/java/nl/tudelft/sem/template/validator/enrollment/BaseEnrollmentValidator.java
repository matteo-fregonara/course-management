package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseEnrollmentValidator implements EnrollmentValidator {

    private transient EnrollmentValidator next;
    protected final transient String authenticationServiceUri = "http://localhost:8081";

    /**
     * Set the next validator in the validation chain. Chain executes until a next that is `null`.
     *
     * @param v the next validator in the chain, null
     * @return validator after setting the next validator
     */
    public EnrollmentValidator setNext(EnrollmentValidator v) {
        this.next = v;

        return this;
    }


    protected void checkNext(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {
        if (next == null) {
            return;
        }

        next.handle(courseCode, netID, request);
    }

}
