package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.service.UserService;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestBody;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.util.ResourceReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private UserService userService;
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        loginController = new LoginController(userService);

        InMemoryUserRepository.truncate();
    }

    @DisplayName("사용자가 로그인에 성공하면 인덱스 페이지로 리다이렉트 되고 쿠키가 설정된다.")
    @Test
    void doPostSuccess() {
        // given
        String account = "testUser";
        String password = "password123";
        String email = "test@example.com";
        userService.registerUser(account, password, email);

        HttpRequest request = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpHeaders("Content-Type: application/x-www-form-urlencoded"),
                new HttpRequestBody(ContentType.FORM_DATA, "account=testUser&password=password123")
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        Assertions.assertAll(
                () -> assertThat(response.toString()).contains("HTTP/1.1 302 Found "),
                () -> assertThat(response.toString()).contains("/index.html"),
                () -> assertThat(response.toString()).contains(Session.JSESSIONID)
        );
    }

    @DisplayName("로그인 실패 시 401 페이지가 반환된다.")
    @Test
    void doPostFailure() {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpHeaders("Content-Type: application/x-www-form-urlencoded"),
                new HttpRequestBody(ContentType.FORM_DATA, "account=testUser&password=wrongPassword")
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response.toString()).contains("HTTP/1.1 401 Unauthorized");
    }

    @DisplayName("세션이 없는 경우 로그인 페이지를 반환한다.")
    @Test
    void doGetWithoutSession() throws Exception {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /login HTTP/1.1"),
                new HttpHeaders(),
                new HttpRequestBody(null)
        );
        HttpResponse response = new HttpResponse();

        String content = ResourceReader.readContent("static/login.html");

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.toString()).contains("HTTP/1.1 200 OK ");
        assertThat(response.toString()).contains(content);
    }
}
