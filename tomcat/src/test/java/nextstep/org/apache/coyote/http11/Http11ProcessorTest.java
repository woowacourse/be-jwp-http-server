package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();
        final List<String> expected = List.of("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        final boolean allMatch = expected.stream()
                .allMatch(actual::contains);
        assertThat(allMatch).isTrue();
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
        final String actual = socket.output();
        final URL resource = ClassLoader.getSystemClassLoader().getResource("static/index.html");
        final List<String> expected = List.of("HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 5564 \r\n",
                "\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        boolean allMatches = expected.stream()
                .allMatch(actual::contains);
        assertThat(allMatches).isTrue();
    }
}
