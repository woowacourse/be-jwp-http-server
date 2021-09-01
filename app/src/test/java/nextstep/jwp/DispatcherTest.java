package nextstep.jwp;

import static nextstep.jwp.RequestHandlerTest.assertResponse;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DispatcherTest {

    private final Assembler container = new Assembler();
    private final Dispatcher dispatcher = container.dispatcher();

    @DisplayName("Controller 요청 처리")
    @Test
    void dispatchController() {
        // given
        final String account = "corgi";
        final String password = "password";
        final String email = "hkkang%40woowahan.com";

        final String requestBody = "account=" + account + "&password=" + password + "&email=" + email;

        RequestLine requestLine = RequestLine.of("POST /register HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);
        String responseAsString = new String(httpResponse.responseAsBytes(), StandardCharsets.UTF_8);

        // then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: index.html \r\n" +
                "\r\n";
        assertThat(responseAsString).isEqualTo(expected);
        InMemoryUserRepository.delete(new User(account, password, email));
    }

    @DisplayName("ResourceHandler 요청 처리")
    @Test
    void dispatchResourceHandler() throws IOException {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);
        String responseAsString = new String(httpResponse.responseAsBytes(), StandardCharsets.UTF_8);

        // then
        assertResponseWithFile(responseAsString, "index.html", HttpStatus.OK);
    }

    @DisplayName("매핑된 handler가 없는 경우 Not found 출력")
    @Test
    void notFound() throws IOException {
        // given
        RequestLine requestLine = RequestLine.of("GET /invalid.html HTTP/1.1");

        HttpRequest httpRequest = new HttpRequest(requestLine, RequestHeaders.of(Collections.emptyList()), "");
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);
        String responseAsString = new String(httpResponse.responseAsBytes(), StandardCharsets.UTF_8);

        // then
        assertResponseWithFile(responseAsString, "404.html", HttpStatus.NOT_FOUND);
    }

    @DisplayName("로그인 실패에 Unauthorized 출력")
    @Test
    void unauthorized() throws IOException {
        // given
        final String requestBody = "account=account&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);
        String responseAsString = new String(httpResponse.responseAsBytes(), StandardCharsets.UTF_8);

        // then
        assertResponseWithFile(responseAsString, "401.html", HttpStatus.UNAUTHORIZED);
    }

    @DisplayName("서버 에러시 INTERNAL SERVER ERROR 출력")
    @Test
    void internalServerError() throws IOException {
        // given
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        Dispatcher dispatcher = new Dispatcher(null, container.viewSolver());

        // when
        dispatcher.dispatch(httpRequest, httpResponse);
        String responseAsString = new String(httpResponse.responseAsBytes(), StandardCharsets.UTF_8);

        // then
        assertResponseWithFile(responseAsString, "500.html", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("쿠키에 Session이 존재하지 않는 경우 SessionId를 응답한다")
    @Test
    void responseSessionId() {
        // given
        RequestLine requestLine = RequestLine.of("GET /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Collections.emptyList());

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        HttpResponse httpResponse = new HttpResponse();

        // when
        dispatcher.dispatch(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHeaderAttribute("Set-Cookie")).isNotNull();
    }

    private void assertResponseWithFile(String response, String filePath, HttpStatus httpStatus) throws IOException {
        // then
        final URL resource = getClass().getClassLoader().getResource("static/" + filePath);
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertResponse(response, httpStatus, body);
    }
}