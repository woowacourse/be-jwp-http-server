package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.apache.catalina.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class LoginControllerTest {
    private LoginController loginController;
    private MockedStatic<InMemoryUserRepository> mockedRepository;

    @BeforeEach
    void setUp() {
        loginController = LoginController.getInstance();
        mockedRepository = mockStatic(InMemoryUserRepository.class);
    }

    @AfterEach
    void clear() {
        mockedRepository.close();
    }

    @DisplayName("성공적인 로그인 요청에 대해 index.html로 리다이랙트한다.")
    @Test
    void loginSuccess() throws IOException {
        // given
        String requestBody = "account=validUser&password=correctPassword";
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));
        HttpResponse httpResponse = new HttpResponse();


        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";
        String expectedCookie = "Set-Cookie: JSESSIONID=";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader,
                expectedCookie
        );
    }

    @DisplayName("로그인이 잘못되면 401.html로 리다이랙트한다.")
    @Test
    void loginFailedInvalidPassword() throws IOException {
        // given
        String requestBody = "account=validUser&password=wrongPassword";
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: 401.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    @DisplayName("쿠키가 없으면 로그인 화면을 응답한다.")
    @Test
    void loginPage() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: login.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    @DisplayName("쿠키가 있으면 대쉬보드 화면을 응답한다.")
    @Test
    void homePage() throws IOException {
        // given
        Session session = Session.createRandomSession();
        session.setAttribute("user", "validUser");
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");

        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

}
