package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @Nested
    class Process {
        @Test
        void 정상적인_HTTP_응답을_처리할_수_있다() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final var expected = List.of(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: 12 \r\n",
                    "\r\n",
                    "Hello world!");

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }
    }

    @Nested
    class GetStaticPage {
        @Test
        void 인덱스_페이지를_응답할_수_있다() throws IOException {
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
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var expected = List.of(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: " + responseBody.getBytes().length + " \r\n",
                    "\r\n",
                    responseBody);

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }

        @Test
        void CSS_파일을_응답할_수_있다() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var expected = List.of(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/css;charset=utf-8 \r\n",
                    "Content-Length: " + responseBody.getBytes().length + " \r\n",
                    "\r\n",
                    responseBody);

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }

        @Test
        void 로그인_페이지를_응답할_수_있다() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var expected = List.of(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: " + responseBody.getBytes().length + " \r\n",
                    "\r\n",
                    responseBody);

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }

        @Test
        void 회원가입_페이지를_응답할_수_있다() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var expected = List.of(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: " + responseBody.getBytes().length + " \r\n",
                    "\r\n",
                    responseBody);

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }
    }

    @Nested
    class Login {
        @Test
        void 로그인_성공_시_인덱스_페이지로_리다이렉트된다() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final var expected = List.of(
                    "HTTP/1.1 302 Found \r\n",
                    "Location: /index.html \r\n",
                    "Content-Length: 0 \r\n",
                    "\r\n");

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }

        @Test
        void 로그인_실패_시_401_페이지로_리다이렉트된다() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=wromgpassword");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final var expected = List.of("HTTP/1.1 302 Found \r\n",
                    "Location: /401.html \r\n",
                    "Content-Length: 0 \r\n",
                    "\r\n");

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }


        @Test
        void 쿠키에_JSESSIONID_값을_저장해_로그인_상태를_유지할_수_있다() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final var expected = List.of(
                    "HTTP/1.1 302 Found \r\n",
                    "Location: /index.html \r\n",
                    "Content-Length: 0 \r\n",
                    "Set-Cookie: JSESSIONID=",
                    "\r\n");

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }
    }

    @Nested
    class Register {
        @Test
        void 회원가입_성공_시_인덱스_페이지로_리다이렉트된다() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=dora&password=password&email=dorachoo%40woowahan.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final var expected = List.of(
                    "HTTP/1.1 302 Found \r\n",
                    "Location: /index.html \r\n",
                    "Content-Length: 0 \r\n",
                    "\r\n");

            expected.forEach(line -> assertThat(socket.output()).contains(line));
        }
    }
}
