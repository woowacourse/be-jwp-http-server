package org.apache.catalina.core.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    public static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public Session findOrCreate(final String id) {
        return findSession(id)
                .orElseGet(this::create);
    }

    private Session create() {
        final Session session = new Session();
        add(session);

        return session;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(SESSIONS.get(id));
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
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
