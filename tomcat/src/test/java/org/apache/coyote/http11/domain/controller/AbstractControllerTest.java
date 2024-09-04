package org.apache.coyote.http11.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AbstractControllerTest {

    private AbstractController controller;

    @BeforeEach
    void setUp() {
        controller = new AbstractController() {
            @Override
            protected HttpResponse doGet(HttpRequest request) {
                return HttpResponse.status(HttpStatus.OK).body("GET request handled").build();
            }
        };
    }

    @Test
    @DisplayName("구현한 HTTP 메서드를 처리한다.")
    void service() throws IOException {
        String requestLine = "GET /index.html HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        String requestMessage = "test body";
        HttpRequest request = new HttpRequest(requestLine, headerLines, requestMessage);

        HttpResponse response = controller.service(request);

        assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getMessageBody()).isEqualTo("GET request handled")
        );
    }

    @Test
    @DisplayName("구현하지 않은 HTTP 메서드를 처리한다.")
    void serviceMethodNotImplemented() throws IOException {
        String requestLine = "POST /index.html HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        String requestMessage = "test body";
        HttpRequest request = new HttpRequest(requestLine, headerLines, requestMessage);

        HttpResponse response = controller.service(request);

        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }
}