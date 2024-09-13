package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodedParserTest {

    @DisplayName("form-url-encoded-body를 파싱할 수 있다.")
    @Test
    void parseQueryString() {
        // given
        String queryString = "account=gugu&password=password";

        // when
        QueryParamParser queryParamParser = new QueryParamParser(queryString);
        Parameter actual = queryParamParser.getParameter();
        // then
        assertAll(
                () -> assertThat(actual.getValue("account")).isEqualTo("gugu"),
                () -> assertThat(actual.getValue("password")).isEqualTo("password")
        );
    }

}