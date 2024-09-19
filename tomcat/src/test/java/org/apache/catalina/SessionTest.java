package org.apache.catalina;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {
    @DisplayName("속성을 가진 세션이 존재한다.")
    @Test
    void getSessionWithAttribute() {
        // given
        Session session = Session.createRandomSession();
        session.setAttribute("user", "account");

        // when
        boolean result = session.hasAttribute("user", "account");

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("속성을 가진 세션이 존재하지 않는다.")
    @Test
    void cannotGetSessionWithAttribute() {
        // given
        Session session = Session.createRandomSession();
        session.setAttribute("user", "account");

        // when
        boolean result = session.hasAttribute("user", "wrongAccount");

        // then
        assertThat(result).isFalse();
    }


    @DisplayName("속성을 가진 세션이 존재하지 않는다. - 속성이 없을 때")
    @Test
    void cannotGetSessionWithoutAttribute() {
        // given
        Session session = Session.createRandomSession();

        // when
        boolean result = session.hasAttribute("user", "account");

        // then
        assertThat(result).isFalse();
    }
}
