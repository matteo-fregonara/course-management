package nl.tudelft.sem.template.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;


//Added PMD supression which was thrown due to the dispatcher variable and "true" string.
//Dataflow anomaly analysis suppressed as the lamda expression in the dispatcher
//and we cannot replace the "true" string literal
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
public class ServiceCommunicationTest {


    transient MockWebServer mockWebServer;
    final transient String testUrl = "http://localhost:8090/testUrl";

    @AfterEach
    void cleanUp() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void validUrl() throws Exception {



        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/testUrl")) {
                    return new MockResponse().setResponseCode(200).setBody("true");
                } else {
                    return new MockResponse().setResponseCode(400);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8090); //Auth port


        String ret = ((String) ServiceCommunication.sendGetRequest(testUrl,
                "no JWT",
                "No error msg",
                String.class));

        assertEquals("true", ret);

    }


    @Test
    void throwExceptionTest() throws Exception {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/testUrl")) {
                    return new MockResponse().setResponseCode(409);
                } else {
                    return new MockResponse().setResponseCode(400);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8090); //Auth port

        assertThrows(ResponseStatusException.class,
                () -> {
                    ServiceCommunication.sendGetRequest(testUrl,
                            "no JWT",
                            "Error message",
                            String.class);
                });

    }

    @Test
    void validJwt() throws Exception {
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().equals("/testUrl")) {
                    if (request.getHeader("Authorization").equals("JWT")) {
                        return new MockResponse().setResponseCode(200).setBody("true");
                    }
                    return new MockResponse().setResponseCode(403);
                } else {
                    return new MockResponse().setResponseCode(400);
                }
            }
        };

        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8090); //Auth port


        String ret = ((String) ServiceCommunication.sendGetRequest(testUrl,
                "JWT",
                "No error msg",
                String.class));


        assertEquals("true", ret);
    }
}
