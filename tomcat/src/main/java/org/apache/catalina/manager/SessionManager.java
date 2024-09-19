package org.apache.catalina.manager;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionManager implements Manager {
    private static final SessionManager instance = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(HttpRequest request) {
        HttpCookie cookie = request.getCookie();
        if (cookie.hasJSessionId()) {
            return Optional.ofNullable(SESSIONS.get(cookie.getJsessionid()));
        }
        return Optional.empty();
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public Optional<Session> getByAttribute(String attribute, Object value) {
        for (Entry<String, Session> entry : SESSIONS.entrySet()) {
            if (entry.getValue().hasAttribute(attribute, value)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }
}
