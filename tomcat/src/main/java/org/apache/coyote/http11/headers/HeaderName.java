package org.apache.coyote.http11.headers;

public enum HeaderName {

    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    ;

    private final String value;

    HeaderName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
