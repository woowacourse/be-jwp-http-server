package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키 문자열을 파싱하여 인스턴스 생성")
    @Test
    void construct_Success() {
        HttpCookie httpCookie = new HttpCookie("name1=value1; name2=value2");
        assertThat(httpCookie.buildMessage())
                .isIn("name1=value1; name2=value2", "name2=value2; name1=value1");
    }

    @DisplayName("쿠키에 세션 추가")
    @Test
    void addSession() {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.setSession("value1");
        assertThat(httpCookie.buildMessage()).isEqualTo("JSESSIONID=value1");
    }
}
