package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
//Gives a false warning when trying to use try and catch clause
public class CanEnrollStatusNumberValidator extends BaseCourseValidator {

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {

        String canEnrollString = request.getParameter("canEnroll");
        boolean canEnroll;
        try {
            canEnroll = Boolean.parseBoolean(canEnrollString);
        } catch (Exception e) {
            String msg = "The specified canEnroll value must be either true or false.";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        super.checkNext(course, request);
    }

}
