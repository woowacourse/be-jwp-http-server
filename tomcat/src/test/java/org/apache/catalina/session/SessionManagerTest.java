package org.apache.catalina.session;

import com.techcourse.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SessionManagerTest {

    private SessionManager sessionManager = SessionManager.getInstance();
    private User user;
    private Session session;
    private String sessionId;

    @BeforeEach
    void setUp() {
        user = new User("test", "password", "test@email.com");

        sessionId = "id";
        session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Test
    @DisplayName("사용자의 JSESSIONID가 저장되어 있다면 해당 session를 반환한다")
    void findSessionId() {
        //when
        Session foundSession = sessionManager.findSession(sessionId);

        //then
        assertThat(foundSession.getAttribute("user")).isEqualTo(user);

    }

    @Test
    @DisplayName("사용자의 JSESSIONID가 저장되어 있지 않다면 null 값을 반환한다")
    void findSessionId_fail() {
        //given
        String notSavedSessionId = "test";

        //when
        Optional<Session> foundSession = Optional.ofNullable(sessionManager.findSession(notSavedSessionId));

        //then
        assertThat(foundSession).isEmpty();
    }
}