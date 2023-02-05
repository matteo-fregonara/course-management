package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

public class RoleCheckValidator extends BaseEnrollmentValidator {

    private transient String roleCheck;

    @Override
    public void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {

        WebClient.Builder webClientBuilder = WebClient.builder();

        roleCheck = webClientBuilder
                .baseUrl(super.authenticationServiceUri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .get()
                .uri("/getRole/" + netID)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        clientResponse -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user");
                        })
                .bodyToMono(String.class)
                .block();

        if (request.getRequestURI().contains("Teacher")) {
            if (!roleCheck.equals("ROLE_TEACHER")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Specified user is not a teacher or does not exist");
            }
        } else if (request.getRequestURI().contains("enroll")
                || request.getRequestURI().contains("removeStudent")) {
            if (!roleCheck.equals("ROLE_STUDENT")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Specified user is not a student or does not exist");
            }
        } else if (request.getRequestURI().contains("createCourse")) {
            // CreateCourse automatically adds teacher to the course
            super.checkNext(courseCode, netID, request);
            return;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Specified user does not exist");
        }
        super.checkNext(courseCode, netID, request);
    }

}
