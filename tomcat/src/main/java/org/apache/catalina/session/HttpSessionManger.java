package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionManger implements Manager {

    private static final HttpSessionManger HTTP_SESSION_MANGER = new HttpSessionManger();
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessionManger() { }

    public static HttpSessionManger getInstance() {
        return HTTP_SESSION_MANGER;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
