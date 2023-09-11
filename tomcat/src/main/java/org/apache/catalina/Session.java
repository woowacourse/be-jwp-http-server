package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session() {
        this(UUID.randomUUID().toString());
    }

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(final String name) {
        return this.values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}
