package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class BodyLineTest {

    @Test
    @DisplayName("바디 줄 파싱 테스트")
    public void bodyLineParsingTest() {

        // given
        final ParsingLine parsingLine = new BodyLine();

        // when
        final ParsingLine nextLine = parsingLine.parse("Hello World!");

        //then
        final HttpRequest actual = new HttpRequestBuilder().body("Hello World!")
                                                           .build();

        final HttpRequest httpRequest = nextLine.buildRequest();
        assertThat(nextLine).isExactlyInstanceOf(BodyLine.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "", " "})
    @DisplayName("입력 라인이 null 혹은 공백일 경우 EndLine 객체 반환")
    public void returnEndLineTest(String body) {

        // given
        if ("null".equals(body)) {
            body = null;
        }

        final ParsingLine parsingLine = new BodyLine();

        // when
        final ParsingLine nextLine = parsingLine.parse(body);

        //then
        final HttpRequest actual = new HttpRequestBuilder().body("Hello World!")
                                                           .build();

        final HttpRequest httpRequest = nextLine.buildRequest();
        assertThat(nextLine).isExactlyInstanceOf(EndLine.class);
        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(actual);
    }
}
