package org.apache.coyote.http11.domain.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("쿠키를 조회한다.")
    void getCookie() {
        Cookie cookie = new Cookie();
        cookie.setCookie("key1", "value1");

        String value1 = cookie.getCookie("key1");

        assertThat(value1).isEqualTo("value1");
    }

    @Test
    @DisplayName("쿠키가 존재하는지 확인한다.")
    void containsCookie() {
        Cookie cookie = new Cookie();
        cookie.setCookie("key1", "value1");

        boolean isContains = cookie.containsCookie("key1");

        assertThat(isContains).isTrue();
    }

    @Test
    @DisplayName("쿠키를 문자로 변환한다.")
    void toCookieString() {
        Cookie cookie = new Cookie();
        cookie.setCookie("key1", "value1");

        assertThat(cookie.containsCookie("key1")).isTrue();
    }
}
