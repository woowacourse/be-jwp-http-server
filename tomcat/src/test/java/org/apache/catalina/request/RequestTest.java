package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestTest {
    @Nested
    @DisplayName("생성")
    class Constructor {
        @Test
        @DisplayName("성공 : Request 규정에 따라 적절하게 주어진 경우 생성 성공")
        void ConstructorSuccess() {
            RequestLine requestLine = new RequestLine("GET /index?account=gugu&password=password HTTP/1.1");
            Map<String, String> headers = Map.of("Content-Length", "text/html", "Cookie", "id=gugu");
            RequestHeader requestHeader = new RequestHeader(headers);
            Map<String, String> body = Map.of("body", "hello");
            RequestBody requestBody = new RequestBody(body);

            Request request = new Request(requestLine, requestHeader, requestBody);

            assertAll(() -> assertThat(request.getQueryParam()).isEqualTo(
                            Map.of("account", "gugu", "password", "password")),
                    () -> assertThat(request.getPathWithoutQuery()).isEqualTo("/index"),
                    () -> assertThat(request.getFileType()).isEqualTo("text/html"),
                    () -> assertThat(request.getCookie()).isEqualTo(new HttpCookie(Map.of("id", "gugu"))),
                    () -> assertThat(request.getBody()).isEqualTo(body));
        }
    }
}
