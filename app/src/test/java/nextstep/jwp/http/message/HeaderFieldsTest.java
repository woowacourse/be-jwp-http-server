package nextstep.jwp.http.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderFieldsTest {

    @DisplayName("HeaderFields 를 생성한다.")
    @Test
    void create() {
        // given
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        // when
        HeaderFields headerFields = new HeaderFields(headerParams);

        // then
        assertThat(headerFields.getFields()).isEqualTo(headerParams);
    }

    @DisplayName("문자열로 HeaderFields 를 생성한다.")
    @Test
    void createWithString() {
        // given
        String headerMessage = String.join("\r\n",
                "Date: Mon, 10 Jul 2000 01:40:10 GMT ",
                "Server: Apache ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "");

        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        HeaderFields expect = new HeaderFields(headerParams);

        // when
        HeaderFields headerFields = HeaderFields.from(headerMessage);

        // then
        assertThat(headerFields).isEqualTo(expect);
    }

    @DisplayName("HeaderFields 를 문자열로 변환한다.")
    @Test
    void asString() {
        // given
        LinkedHashMap<String, String> headerParams = new LinkedHashMap<>();
        headerParams.put("Date", "Mon, 10 Jul 2000 01:40:10 GMT");
        headerParams.put("Server", "Apache");
        headerParams.put("Content-Type", "text/html;charset=utf-8");
        headerParams.put("Content-Length", "12");

        String expected = "Date: Mon, 10 Jul 2000 01:40:10 GMT\r\n" +
                "Server: Apache\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 12\r\n";

        HeaderFields headerFields = new HeaderFields(headerParams);

        // when
        String headerMesssage = headerFields.asString();

        // then
        assertThat(headerMesssage).isEqualTo(expected);
    }
}
