package org.apache.coyote.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterHandlerTest {

    @Test
    @DisplayName("GET 요청 처리: 회원가입 페이지 반환")
    void handle_GetRequest() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/register.html");
        final String expectedResponseBody = Files.readString(Path.of(resourceURL.getPath()));

        final RequestLine requestLine = new RequestLine("GET", "/register", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);

        assertThat(RegisterHandler.getInstance().handle(request)).contains(expectedResponseBody);
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 회원가입 정보로 회원가입 성공")
    void handle_PostRequest_WithValidRegistration() {
        final RequestLine requestLine = new RequestLine("POST", "/register", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=newuser&email=newuser@example.com&password=password123");

        final String result = RegisterHandler.getInstance().handle(request);

        assertAll(
                () -> assertThat(result).contains("302 Found"),
                () -> assertThat(result).contains("http://localhost:8080/index.html")
        );
    }
}
