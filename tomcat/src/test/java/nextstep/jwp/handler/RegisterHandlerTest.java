package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpRequestHeader;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterHandlerTest {

    private static final HttpRequestHeader EMPTY_REQUEST_HEADER = new HttpRequestHeader(List.of());

    @DisplayName(value = "GET 요청 시 응답값에 register 페이지 포함")
    @Test
    void getRegisterPage() {
        // given
        final HttpRequest request = new HttpRequest("GET /register HTTP/1.1 ", EMPTY_REQUEST_HEADER,
                new HttpRequestBody(""));
        final RegisterHandler registerHandler = new RegisterHandler();

        final String expectedStatusLine = "HTTP/1.1 200 OK ";
        final String expectedMessageBody = "<title>회원가입</title>";

        // when
        final HttpResponse response = registerHandler.register(request);

        // then
        final String actual = response.generateResponse();
        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).contains(expectedMessageBody);
    }

    @DisplayName(value = "등록 성공 시 응답값의 헤더에 Location: /login.html 포함")
    @Test
    void postRegister() {
        // given
        final HttpRequest request = new HttpRequest("POST /register HTTP/1.1 ", EMPTY_REQUEST_HEADER,
                new HttpRequestBody("account=gugu&password=password&email=hkkang%40woowahan.com"));
        final RegisterHandler registerHandler = new RegisterHandler();

        final String expectedStatusLine = "HTTP/1.1 302";
        final String expectedMessageBody = "Location: /login.html ";

        // when
        final HttpResponse response = registerHandler.register(request);

        // then
        final String actual = response.generateResponse();
        assertThat(actual).contains(expectedStatusLine);
        assertThat(actual).contains(expectedMessageBody);
    }
}
