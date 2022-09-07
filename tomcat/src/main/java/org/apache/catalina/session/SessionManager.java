package org.apache.catalina.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.user.User;
import org.apache.catalina.Manager;
import org.apache.catalina.session.exception.InvalidSessionIdException;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.http11response.ResponseComponent;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final String USER_KEY = "user";
    private static final String NEW_KEY = "new";
    private static final String JSESSIONID = "JSESSIONID";

    private SessionManager() {}

    public static SessionManager connect() {
        return sessionManager;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws InvalidSessionIdException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public String createSession() {
        Session session = new Session();
        add(session);
        return session.getId();
    }

    public Session generateSession(User user) {
        Session session = new Session();
        session.setAttribute(USER_KEY, user);
        return session;
    }

    public void addUserInSession(String sessionId, User user) {
        Session session = findSession(sessionId);
        session.setAttribute(USER_KEY, user);
    }

    public boolean checkLogin(String sessionId) {
        Session session = findSession(sessionId);
        return session.hasAttribute(USER_KEY);
    }
}
