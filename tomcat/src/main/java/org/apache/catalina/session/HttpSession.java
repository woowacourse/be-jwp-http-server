package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values;

    public HttpSession(final String name, final Object value) {
        this.id = UUID.randomUUID().toString();
        values = new HashMap<>();
        values.put(name, value);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void addAttribute(final String name, final Object value) {
        values.put(name, value);
    }

}
