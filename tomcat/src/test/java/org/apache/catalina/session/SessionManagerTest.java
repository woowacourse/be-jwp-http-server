package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @Test
    void 세션을_추가하고_조회할_수_있다() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session();

        // when
        sessionManager.add(session);
        Optional<Session> retrievedSession = sessionManager.findSession(session.getId());

        // then
        assertThat(retrievedSession).isPresent();
        assertThat(retrievedSession.get().getId()).isEqualTo(session.getId());
    }

    @Test
    void 존재하지_않는_세션을_조회하면_빈_Optional을_반환한다() {
        // given
        SessionManager sessionManager = new SessionManager();

        // when
        Optional<Session> session = sessionManager.findSession("nonExistingId");

        // then
        assertThat(session).isEmpty();
    }

    @Test
    void 동일한_ID의_세션을_덮어쓸_수_있다() {
        // given
        SessionManager sessionManager = new SessionManager();
        String sessionId = "duplicateId";
        Session firstSession = new Session(sessionId);
        Session secondSession = new Session(sessionId);

        // when
        sessionManager.add(firstSession);
        sessionManager.add(secondSession);

        // then
        Optional<Session> retrievedSession = sessionManager.findSession(sessionId);
        assertThat(retrievedSession).isPresent();
        assertThat(retrievedSession.get()).isEqualTo(secondSession);
    }
}
