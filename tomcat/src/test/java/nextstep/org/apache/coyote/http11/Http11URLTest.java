package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.Http11URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11URLTest {
    private StubSocket stubSocket;

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void isDefault() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(request);
        final Http11URL http11URL = Http11URL.of(stubSocket.getInputStream());

        // when
        final boolean actual = http11URL.isDefault();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void getMediaType() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(request);
        final Http11URL http11URL = Http11URL.of(stubSocket.getInputStream());

        // when
        final String actual = http11URL.getMediaType();

        // then
        assertThat(actual).isEqualTo("text/css");
    }


    @Test
    void getMediaType_default() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(request);
        final Http11URL http11URL = Http11URL.of(stubSocket.getInputStream());

        // when
        final String actual = http11URL.getMediaType();

        // then
        assertThat(actual).isEqualTo("text/html");
    }

    @Test
    void parseLogin() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(request);
        final Http11URL http11URL = Http11URL.of(stubSocket.getInputStream());

        // when
        final String actual = http11URL.getPath();

        // then
        assertThat(actual).isEqualTo("/login.html");
    }
}
