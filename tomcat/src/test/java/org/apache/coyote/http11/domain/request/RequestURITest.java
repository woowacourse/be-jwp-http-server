package org.apache.coyote.http11.domain.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURITest {

    @Test
    @DisplayName("RequestURI 를 생성한다.")
    void createRequestURI() {
        String requestURIString = "/index.html?name=lee&age=20";
        String expectedPath = "/index.html";
        Map<String, String> expectedQueryParameters = Map.of("name", "lee", "age", "20");

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters())
                        .containsExactlyInAnyOrderEntriesOf(expectedQueryParameters)
        );
    }

    @Test
    @DisplayName("쿼리스트링이 없는 RequestURI 를 생성한다.")
    void createRequestURIWithoutQueryString() {
        String requestURIString = "/index.html";
        String expectedPath = "/index.html";

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters()).isEmpty()

        );
    }

    @Test
    @DisplayName("쿼리스트링의 형식이 맞지 않는 RequestURI 를 생성한다.")
    void createRequestURIWithInvalidQueryString() {
        String requestURIString = "/index.html?keyWithoutValue=&=valueWithoutKey&key==&keyValue";
        String expectedPath = "/index.html";

        RequestURI requestURI = new RequestURI(requestURIString);

        assertAll(
                () -> assertThat(requestURI.getPath()).isEqualTo(expectedPath),
                () -> assertThat(requestURI.getQueryParameters()).isEmpty()
        );
    }
}