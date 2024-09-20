package org.apache.catalina.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static SessionManager SESSION_MANAGER;

    private SessionManager() {
    }

    @Override
    public void add(final Session session) {
        if (session.isInvalidate()) {
            throw new IllegalStateException("무효화 된 세션입니다.");
        }
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) throws IOException {
        if (SESSIONS.containsKey(id)) {
            return validateSession(SESSIONS.get(id));
        }
        throw new IOException("세션 id가 존재하지 않습니다.");
    }

    public boolean isExistSessionId(final String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    private Session validateSession(final Session session) {
        if (session.isInvalidate()) {
            remove(session);
            throw new IllegalStateException("무효화 된 세션입니다.");
        }
        return session;
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public static synchronized SessionManager getInstance() {
        if (SESSION_MANAGER == null) {
            return new SessionManager();
        }
        return SESSION_MANAGER;
    }
}
