package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.CharsetType;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        String body = buildRequestBody(Map.of("account", "gugu", "password", "password"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when
        loginController.service(request, response);

        // then
        String expectedRequestLine = HttpVersion.HTTP_1_1.getVersionString() + " " + HttpStatusCode.FOUND.toStatus();
        String expectedLocationHeader = "Location: " + "/index.html";
        String expectedContentType =
                "Content-Type: " + MimeType.HTML.getMimeType() + "; charset=" + CharsetType.UTF_8.getCharset();

        System.out.println(response);
        System.out.println(expectedContentType);
        assertAll(
                () -> assertThat(response.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedLocationHeader.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedContentType.getBytes())
        );
    }

    private String buildRequestBody(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((param1, param2) -> param1 + "&" + param2)
                .orElse("");
    }

    private HttpRequest buildHttpRequest(String method, String path, String body) throws IOException {
        String requestLine = String.join(" ", method, path, "HTTP/1.1");

        String httpRequest = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequest(inputStream);
    }

    @DisplayName("로그인에 실패할 경우, 401페이지로 리다이렉트한다.")
    @Test
    void failLogin() throws IOException {
        // given
        String body = buildRequestBody(Map.of("account", "gugu", "password", "invalidPassword"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when
        loginController.service(request, response);

        // then
        System.out.println(response);
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocationHeader = "Location: " + "/401.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getMimeType();

        assertAll(
                () -> assertThat(response.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedLocationHeader.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedContentType.getBytes())
        );
    }

    @DisplayName("사용자를 찾을 수 없을 경우, 예외를 발생한다.")
    @Test
    void userNotFound() throws IOException {
        // given
        String body = buildRequestBody(Map.of("account", "gugugu", "password", "invalidPassword"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when&then
        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("account 없는 body일 경우, 예외를 발생한다.")
    @Test
    void validateBody_AccountMissing() throws IOException {
        // given
        String body = buildRequestBody(Map.of("password", "invalidPassword"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when&then
        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account가 존재하지 않습니다.");
    }

    @DisplayName("password 없는 body일 경우, 예외를 발생한다.")
    @Test
    void validateBody_PasswordMissing() throws IOException {
        // given
        String body = buildRequestBody(Map.of("account", "gugu"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when&then
        assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password가 존재하지 않습니다.");
    }
}
