package nextstep.jwp.web.network;

import nextstep.jwp.web.network.request.HttpMethod;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;
import static nextstep.jwp.web.network.HttpUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpRequestTest {

    @DisplayName("HttpRequest 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final InputStream inputStream = RequestBuilder
                .builder(HttpMethod.GET, "/register")
                .localHost8080()
                .connectionKeepAlive()
                .acceptAll()
                .buildInputStream();

        // when // then
        assertThatCode(() -> new HttpRequest(inputStream))
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpRequest에서 method를 추출한다 - 성공")
    @Test
    void getHttpMethod() {
        // given
        final HttpRequest request = RequestBuilder
                .builder(HttpMethod.GET, "/register")
                .localHost8080()
                .connectionKeepAlive()
                .acceptAll()
                .buildRequest();

        // when
        final HttpMethod actual = request.getHttpMethod();

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("HttpRequest에서 URI를 추출한다 - 성공")
    @Test
    void getURI() {
        // given
        final HttpRequest request = RequestBuilder
                .builder(HttpMethod.GET, "/register")
                .localHost8080()
                .connectionKeepAlive()
                .acceptAll()
                .buildRequest();

        // when
        final URI actual = request.getURI();

        // then
        assertThat(actual).isEqualTo(new URI("/register"));
    }

    @DisplayName("HttpRequest에서 body를 추출한다 - 성공")
    @Test
    void getBody() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final HttpRequest request = RequestBuilder
                .builder(HttpMethod.POST, "/register")
                .localHost8080()
                .connectionKeepAlive()
                .contentType(ContentType.FORM)
                .contentLength(58)
                .acceptAll()
                .body(requestBody)
                .buildRequest();

        // when
        final Map<String, String> actual = request.getBodyAsMap();

        // then
        assertThat(actual)
                .contains(entry("account", "gugu"), entry("password", "password"), entry("email", "hkkang%40woowahan.com"));
    }

    @Test
    @DisplayName("HttpRequest 객체로부터 HttpSession을 가지고 온다 - 성공")
    void getSession() {
        // given
        final UUID id = UUID.randomUUID();
        final HttpRequest request = RequestBuilder
                .builder(HttpMethod.GET, "/register")
                .localHost8080()
                .connectionKeepAlive()
                .jsessionid(id)
                .acceptAll()
                .buildRequest();
        final HttpSession session = new HttpSession(id);
        HttpSessions.setSession(session);

        // when
        final HttpSession actual = request.getSession();

        // then
        assertThat(actual).isEqualTo(session);
    }
}