package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private static SessionManager INSTANCE;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
