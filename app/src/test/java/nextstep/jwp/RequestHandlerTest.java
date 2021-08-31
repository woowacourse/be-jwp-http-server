package nextstep.jwp;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(LINE_SEPARATOR,
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String expected = "HTTP/1.1 200 OK\n" +
                "Content-Length: " + expectedBody.length + LINE_SEPARATOR +
                "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR +
                "\n" +
                new String(expectedBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
