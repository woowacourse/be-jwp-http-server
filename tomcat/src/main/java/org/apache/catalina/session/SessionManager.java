package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
