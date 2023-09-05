package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    public static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public Session findOrCreate(final String id) {
        return findSession(id)
                .orElseGet(this::create);
    }

    private Session create() {
        final Session session = new Session();
        SESSIONS.put(session.getId(), session);

        return session;
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public static class Session {

        private final String id;
        private final Map<String, Object> values = new HashMap<>();

        private Session() {
            this.id = UUID.randomUUID().toString();
        }

        public String getId() {
            return id;
        }

        public Object getAttribute(final String name) {
            return values.get(name);
        }

        public void setAttribute(final String name, final Object value) {
            values.put(name, value);
        }

    }
}
