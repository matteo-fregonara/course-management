package nl.tudelft.sem.template.validator.courses;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.communication.ServiceCommunication;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.DataflowAnomalyAnalysis"})
// Dataflow anomaly analysis suppressed
// BodyToMono throws exceptions when cannot handle therefore response cannot be null
@Component
public class RoomAvailabilityValidator extends BaseCourseValidator {

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {
        try {
            String roomCodeString = Integer.toString(course.getRoomCode());
            roomAvailabilityCheck(roomCodeString, request);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(course, request);
    }

    private void roomAvailabilityCheck(String roomCodeString, HttpServletRequest request)
            throws ResponseStatusException {

        String roomAvailResponse = (String) ServiceCommunication.sendGetRequest(
            super.roomServiceUri + "/available/" + roomCodeString,
            request.getHeader("Authorization"),
            "No such room",
            String.class);

        if (roomAvailResponse == null || !roomAvailabilityParser(roomAvailResponse)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not available");
        }
    }

    private boolean roomAvailabilityParser(String roomAvailResponse) {
        return roomAvailResponse.toLowerCase(Locale.ENGLISH).contains("true");
    }
}
