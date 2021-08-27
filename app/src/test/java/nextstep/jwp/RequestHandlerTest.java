package nextstep.jwp;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = "Hello world!";
        String expected = toHttp200TextHtmlResponse(expectResponseBody);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void mainPage() throws IOException {
        // given
        final String requestUri = "/";
        final String httpRequest = toHttpGetRequest(requestUri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = "Hello world!";
        String expected = toHttp200TextHtmlResponse(expectResponseBody);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String requestUri = "/index.html";
        final String httpRequest = toHttpGetRequest(requestUri);
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = Files.readString(new File(resource.getFile()).toPath());
        String expected = toHttp200TextHtmlResponse(expectResponseBody);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loginForm() throws IOException {
        // given
        final String requestUri = "/login";
        final String httpRequest = toHttpGetRequest(requestUri);
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String expectResponseBody = Files.readString(new File(resource.getFile()).toPath());
        String expected = toHttp200TextHtmlResponse(expectResponseBody);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loginRequest() throws IOException {
        // given
        final String requestUri = "/login?account=gugu&password=password";
        final String httpRequest = toHttpGetRequest(requestUri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String redirectUrl = "/index.html";
        String expected = toHttp302Response(redirectUrl);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 패스워드를_잘못_입력시_실패한다() throws IOException {
        // given
        final String requestUri = "/login?account=gugu&password=passwor";
        final String httpRequest = toHttpGetRequest(requestUri);
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        final String redirectUrl = "/401.html";
        String expected = toHttp302Response(redirectUrl);
        // when
        requestHandler.run();
        final String actual = socket.output();
        // then
        assertThat(actual).isEqualTo(expected);
    }

    private String toHttpGetRequest(String requestUri) {
        return String.join("\r\n",
                "GET " + requestUri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    private String toHttp200TextHtmlResponse(String expectResponseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + expectResponseBody.getBytes().length + " " ,
                "",
                expectResponseBody);
    }

    private String toHttp302Response(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: http://localhost:8080" + redirectUrl);
    }

    private String toHttp401Response(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
