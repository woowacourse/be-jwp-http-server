package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static boolean contains(final String id) {
        return SESSIONS.containsKey(id);
    }

    public static void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public static int getSize() {
        return SESSIONS.size();
    }

    private SessionManager() {
    }
}
