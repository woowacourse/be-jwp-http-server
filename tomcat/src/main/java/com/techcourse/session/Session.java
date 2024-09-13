package com.techcourse.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, String> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, String value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }
}
