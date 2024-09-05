package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatusCode = HttpStatusCode.OK.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatusCode)
                .contains(expectedContentType);
    }

    @Test
    void cssTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatusCode = HttpStatusCode.OK.getValue();
        String expectedContentType = "text/css";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatusCode)
                .contains(expectedContentType);
    }

    @Test
    void loginHtmlTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatusCode = HttpStatusCode.OK.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatusCode)
                .contains(expectedContentType);
    }

    @Test
    void loginSuccessTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatusCode = HttpStatusCode.FOUND.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatusCode)
                .contains(expectedContentType);
    }

    @Test
    void loginFailTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=notpassword HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatusCode = HttpStatusCode.UNAUTHORIZED.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatusCode)
                .contains(expectedContentType);
    }
}
