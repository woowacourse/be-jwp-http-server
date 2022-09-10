package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
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
        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
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

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + expectedBody.getBytes().length + " ",
                "",
                expectedBody);

        String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void css파일을_호출할_수_있다() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void js_파일을_호출할_수_있다() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 잘못된_파일_형식은_호출할_수_없다() {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /index.hi HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /404.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");
        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 없는_파일은_호출할_수_없다() {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /invalidFile.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /404.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 가입된_유저는_로그인을_할_수_있다() {
        //given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expectRequestLine = "HTTP/1.1 302 Found ";
        var expectedHeaderAndBody = String.join("\r\n",
                "Location: /index.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertAll(
                () -> assertThat(actual).startsWith(expectRequestLine),
                () -> assertThat(actual).contains("Set-Cookie: JSESSIONID="),
                () -> assertThat(actual).contains(expectedHeaderAndBody)
        );
    }

    @Test
    void 없는_아이디로_로그인_시_로그인에_실패한다() {
        //given
        final String body = "account=gogo&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 잘못된_비밀번호로_로그인_시_로그인에_실패한다() {
        //given
        final String body = "account=gugu&password=invalidPassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 로그인_요청_시_account를_보내지_않으면_로그인에_실패한다() {
        //given
        final String body = "id=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 로그인_요청_시_password를_보내지_않으면_401_html을_응답한다() {
        //given
        final String body = "account=gugu&password=";
        final String invalidRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(invalidRequestMessage);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.run();

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        String actual = socket.output();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void register로_요청_시_register_html_파일을_반환한다() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        String actual = socket.output();

        assertThat(actual).contains(expected);
    }
}
