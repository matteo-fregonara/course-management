package nl.tudelft.sem.template.validator.courses;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.communication.ServiceCommunication;
import nl.tudelft.sem.template.entities.Course;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RoomFitValidator extends BaseCourseValidator {

    private final transient WebClient.Builder webClientBuilder = WebClient.builder();

    @Override
    public void handle(Course course, HttpServletRequest request) throws ResponseStatusException {
        try {
            String roomCodeString = Integer.toString(course.getRoomCode());
            roomFitCheck(roomCodeString, course.getMaxSize(), request);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getReason());
        }

        super.checkNext(course, request);
    }

    private void roomFitCheck(String roomCodeString, int size, HttpServletRequest request) {

        String roomFitResponse = (String) ServiceCommunication.sendGetRequest(
            super.roomServiceUri + "/maxSize/" + roomCodeString,
            request.getHeader("Authorization"),
            "",
            String.class);

        if (roomFitResponse == null || !roomFitParser(roomFitResponse, size)) {
            StringBuilder str = new StringBuilder("Max students in course exceed room size: ");
            String reason = str
                    .append("This room fits ")
                    .append(roomFitResponse)
                    .append(" people, but a course of ")
                    .append(size)
                    .append(" people is submitted.")
                    .toString();

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }

    private boolean roomFitParser(String roomFitResponse, int size) {
        int maxSize = Integer.parseInt(roomFitResponse);
        return size <= maxSize;
    }

}
