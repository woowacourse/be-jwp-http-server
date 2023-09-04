package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        this.values.put(name, value);
    }

    public void removeAttribute(final String name) {
        this.values.remove(name);
    }

    public void invalidate() {
        this.values.clear();
    }
}
