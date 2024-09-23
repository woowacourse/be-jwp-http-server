package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String KEY_DELIMITER = "=";
    private static final String SPACE = " ";

    private final String id;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static String getSessionKey() {
        return JSESSIONID;
    }

    public String toHeader(String uuid) {
        return JSESSIONID + KEY_DELIMITER + uuid + SPACE;
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    public String getId() {
        return id;
    }
}
