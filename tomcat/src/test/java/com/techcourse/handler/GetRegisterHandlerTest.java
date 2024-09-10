package com.techcourse.handler;

import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpXW3UrlEncodedBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetRegisterHandlerTest {

    @Test
    @DisplayName("회원가입 관련 GET 요청을 처리할 수 있다.")
    void canHandle() {
        GetRegisterHandler getRegisterHandler = new GetRegisterHandler();

        boolean result = getRegisterHandler.canHandle(createHttpRequest("GET /register HTTP/1.1"));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원가입 관련 GET 요청이 아니라면 처리할 수 없다.")
    void cantHandle() {
        GetRegisterHandler getRegisterHandler = new GetRegisterHandler();

        boolean result = getRegisterHandler.canHandle(createHttpRequest("POST /register HTTP/1.1"));

        assertThat(result).isFalse();
    }

    private HttpRequest createHttpRequest(String startLine) {
        return HttpRequest.createHttp11Message(startLine, Header.empty(), new HttpXW3UrlEncodedBody(null));
    }
}
