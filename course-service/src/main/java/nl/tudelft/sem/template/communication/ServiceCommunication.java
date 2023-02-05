package nl.tudelft.sem.template.communication;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

public final class ServiceCommunication {

    /**
     * Sends a get request to specific url.
     *
     * @param uri The uri to input
     * @param jwt The JWT authentication token
     * @param errorMsg Correct error message
     * @param elementClass The class to input
     * @return A Get WebClient get request.
     */
    public static Object sendGetRequest(String uri,
                                        String jwt,
                                        String errorMsg,
                                        Class<?> elementClass) throws ResponseStatusException {

        return WebClient.builder()
                .baseUrl(uri)
                .build()
                .get()
                .header("Authorization", jwt)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.CONFLICT.value(),
                    clientResponse -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
                    })
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                    clientResponse -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
                    })
                .onStatus(httpStatus -> httpStatus.value() != HttpStatus.OK.value(),
                    clientResponse -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Unknown Error");
                    })
                .bodyToMono(elementClass)
                .block();
    }
}
