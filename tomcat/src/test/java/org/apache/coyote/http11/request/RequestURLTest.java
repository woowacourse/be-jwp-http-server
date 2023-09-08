package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.InvalidRequestLineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RequestURLTest {

    static Stream<Arguments> firstLines() {
        return Stream.of(
                Arguments.of("GET /ocean?donghae=1 HTTP/1.1"),
                Arguments.of("GET /ocean HTTP/1.1"),
                Arguments.of("GET /ocean.html HTTP/1.1")
        );
    }

    @ParameterizedTest(name = "{index} {0} RequestUrl을 생성 성공 테스트")
    @MethodSource("firstLines")
    void from(final String firstLine) {
        // given
        BufferedReader br = new BufferedReader(new StringReader(firstLine));

        // when & then
        assertDoesNotThrow(() -> RequestURL.from(br));
    }

    @Test
    @DisplayName("RequestUrl을 생성 실패 테스트")
    void from_exception() {
        // given
        BufferedReader br = new BufferedReader(new StringReader(" "));
        // when & then
        assertThatThrownBy(() -> RequestURL.from(br))
                .isInstanceOf(InvalidRequestLineException.class)
                .hasMessage("잘못된 RequestURL입니다.");
    }

    @ParameterizedTest(name = "{index} {0} 절대 경로 조회 테스트.")
    @MethodSource("firstLines")
    void getAbsolutePath(final String firstLine) throws IOException {
        // given
        BufferedReader br = new BufferedReader(new StringReader(firstLine));
        final RequestURL requestURL = RequestURL.from(br);

        // when
        final String absolutePath = requestURL.getAbsolutePath();

        // then
        assertThat(absolutePath).isEqualTo("/ocean.html");
    }

    @Test
    @DisplayName("확장자 조회 테스트")
    void getExtension() throws IOException{
        // given
        final String firstLine = "GET /ocean.css HTTP/1.1";
        BufferedReader br = new BufferedReader(new StringReader(firstLine));
        final RequestURL requestURL = RequestURL.from(br);

        // when
        final String extension = requestURL.getExtension();

        // then
        assertThat(extension).isEqualTo("css");
    }
}
