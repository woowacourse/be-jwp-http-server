package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session createRandomSession() {
        return new Session(UUID.randomUUID().toString());
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public String getId() {
        return id;
    }
}
