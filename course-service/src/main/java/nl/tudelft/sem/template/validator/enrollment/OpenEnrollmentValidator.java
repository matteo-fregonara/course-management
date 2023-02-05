package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OpenEnrollmentValidator extends BaseEnrollmentValidator {

    private final transient CourseRepository courseRepository;


    @Autowired
    public OpenEnrollmentValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {

        try {
            preExistingEnrollmentCheck(courseCode, netID);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(courseCode, netID, request);
    }



    private void preExistingEnrollmentCheck(int courseCode, String netID)
            throws ResponseStatusException {
        boolean canEnroll = courseRepository.canEnroll(courseCode);
        if (!canEnroll) {
            String reason = "Course " + courseCode + " is not open for enrollment";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }

}