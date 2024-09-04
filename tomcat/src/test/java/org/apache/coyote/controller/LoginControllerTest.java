package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.MimeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("로그인 컨트롤러 테스트")
class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
    }

    @DisplayName("로그인 요청일 경우, 로그인을 시도한다.")
    @Test
    void login() throws IOException {
        // given
        String path = "/login";
        String queryKey1 = "account";
        String queryValue1 = "gugu";
        String queryKey2 = "password";
        String queryValue2 = "password";

        String queryString = String.join("&",
                queryKey1 + "=" + queryValue1,
                queryKey2 + "=" + queryValue2
        );

        String requestLine = String.join(" ",
                "GET",
                path + "?" + queryString,
                "HTTP/1.1"
        );

        String httpRequest = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Connection: keep-alive",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = new HttpRequest(inputStream);

        // when
        HttpResponse httpResponse = loginController.run(request);

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocation = "Location: /index.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getContentType();

        assertAll(
                () -> assertThat(httpResponse.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedLocation.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedContentType.getBytes())
        );
    }

    @DisplayName("로그인에 실패할 경우, 401패이지로 리다이렉트한다.")
    @Test
    void failLogin() throws IOException {
        // given
        String path = "/login";
        String queryKey1 = "account";
        String queryValue1 = "gugu";
        String queryKey2 = "password";
        String queryValue2 = "invalidPassword";

        String queryString = String.join("&",
                queryKey1 + "=" + queryValue1,
                queryKey2 + "=" + queryValue2
        );

        String requestLine = String.join(" ",
                "GET",
                path + "?" + queryString,
                "HTTP/1.1"
        );

        String httpRequest = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = new HttpRequest(inputStream);

        // when
        HttpResponse httpResponse = loginController.run(request);

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocation = "Location: /index.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getContentType();

        assertAll(
                () -> assertThat(httpResponse.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedLocation.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedContentType.getBytes())
        );
    }
}
