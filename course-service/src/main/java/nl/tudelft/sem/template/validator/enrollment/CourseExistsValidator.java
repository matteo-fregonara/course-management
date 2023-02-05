package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CourseExistsValidator extends BaseEnrollmentValidator {

    private final transient CourseRepository courseRepository;

    @Autowired
    public CourseExistsValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {
        try {
            preExistingCourseCheck(courseCode);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(courseCode, netID, request);
    }

    private void preExistingCourseCheck(int courseCode) throws ResponseStatusException {
        if (!courseRepository.existsById(courseCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course doesn't exist");
        }
    }



}
