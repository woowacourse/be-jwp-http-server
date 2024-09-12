package org.apache.coyote.http11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Nested
    class 정적_파일을_기져온다 {

        @Test
        void index_페이지를_가져온다() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n"+
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void css_파일을_가져온다() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/css,*/*;q=0.1 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/css;charset=utf-8 \r\n" +
                    "Content-Length: 211991 \r\n" +
                    "\r\n"+
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void js_파일을_가져온다() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                    "GET /js/scripts.js HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/js,*/*;q=0.1 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/js;charset=utf-8 \r\n" +
                    "Content-Length: 976 \r\n" +
                    "\r\n"+
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    class 페이지를_가져온다 {

        @Test
        void 로그인_페이지를_가져온다() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/html,*/*;q=0.1 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 3797 \r\n" +
                    "\r\n"+
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 로그인에_성공하면_메인_페이지를_가져온다() {
            // given
            final String httpRequest= String.join("\r\n",
                    "POST /login?account=gugu&password=password HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/html,*/*;q=0.1 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String actual = socket.output();
            Assertions.assertAll(
                    () -> assertThat(actual.startsWith("HTTP/1.1 302 FOUND \r\n")).isTrue(),
                    () -> assertThat(actual.contains("Location: /index.html \r\n")).isTrue()
            );
        }
    }
}


