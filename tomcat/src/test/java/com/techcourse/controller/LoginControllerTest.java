package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.catalina.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
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
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        String body = "account=validUser&password=correctPassword";
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(body), new RequestBody(body));
        HttpResponse httpResponse = new HttpResponse();

        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        // when
        loginController.service(httpRequest, httpResponse);

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

    @DisplayName("세션이 존재하는 로그인 요청에 대해 기존 세션을 제거하고, 새로운 세션을 발급한다.")
    @Test
    void loginSuccessWithCookie() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        Session session = getSession(new User("user", "password", "email"));
        String body = "account=validUser&password=correctPassword";
        HttpHeaders headers = createHttpHeadersWithSession(session);
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody(body));
        HttpResponse httpResponse = new HttpResponse();

        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        // when
        loginController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";
        String expectedCookie = "Set-Cookie: JSESSIONID=";
        String unexpectedCookie = "Set-Cookie: JSESSIONID=" + session.getId();

        assertAll(
                () -> assertThat(httpResponse.serialize()).contains(
                        expectedResponseLine,
                        expectedLocationHeader,
                        expectedCookie
                ).doesNotContain(unexpectedCookie),
                () -> assertThat(SessionManager.getInstance().findSession(httpRequest)).isEmpty()
        );
    }

    @DisplayName("로그인이 잘못되면 401.html로 리다이랙트한다.")
    @Test
    void loginFailedInvalidPassword() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        String body = "account=validUser&password=wrongPassword";
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(body), new RequestBody(body));
        HttpResponse httpResponse = new HttpResponse();

        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        // when
        loginController.service(httpRequest, httpResponse);

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
        RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1 ");
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(null), new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.service(httpRequest, httpResponse);

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
        Session session = getSession(new User("user", "password", "email"));
        RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1 ");
        HttpHeaders headers = createHttpHeadersWithSession(session);
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    private HttpHeaders createHttpHeadersWithSession(Session session) {
        return HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + HttpCookie.ofJSessionId(session.getId())
        ));
    }

    private static Session getSession(User user) {
        Session session = Session.createRandomSession();
        session.setAttribute("user", user);
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return session;
    }

    private HttpHeaders createHeaders(String body) {
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        if (Objects.nonNull(body)) {
            headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        }
        return headers;
    }
}
