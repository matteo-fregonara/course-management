package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.communication.ServiceCommunication;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
// Clear the course-room association at the room service
public class ClearRoomValidator extends BaseCourseValidator {

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {


        ServiceCommunication.sendGetRequest(
            super.roomServiceUri + "/clearRoom/" + course.getRoomCode(),
            request.getHeader("Authorization"),
            "The specified course does not exist.",
            String.class);

        super.checkNext(course, request);
    }

}
