package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Map<String, String> values;

    public Headers() {
        values = new HashMap<>();
    }

    public Headers(Map<String, String> headers) {
        values = headers;
    }

    public void add(String name, String value) {
        values.put(name, value);
    }

    public void addLocation(String location) {
        values.put("Location", location);
    }

    public void addSetCookie(String cookie) {
        values.put("Set-Cookie", cookie);
    }

    public boolean hasContentLength() {
        return values.containsKey("Content-Length");
    }

    public String find(String headerName) {
        return values.get(headerName);
    }

    public String getLocation() {
        return values.get("Location");
    }

    public Cookies getCookie() {
        return Cookies.from(values.getOrDefault("Cookie", ""));
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "";
        }
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
    }
}
