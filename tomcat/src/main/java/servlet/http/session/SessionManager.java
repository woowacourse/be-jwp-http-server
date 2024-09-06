package servlet.http.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new HashMap<>();

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
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }
}
