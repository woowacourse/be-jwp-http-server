package org.apache.coyote.manager;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.session.Session;

public class SessionManager implements Manager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static SessionManager instance;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            return new SessionManager();
        }
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
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean isSessionExist(String id) {
        return SESSIONS.containsKey(id);
    }

    public String generateSession(User user) {
        Session session = new Session(user);
        add(session);
        return session.getId();
    }
}
