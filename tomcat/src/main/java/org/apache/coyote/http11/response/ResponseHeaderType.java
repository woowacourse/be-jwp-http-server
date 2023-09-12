package org.apache.coyote.http11.response;

public enum ResponseHeaderType {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ALLOW("Allow");

    private final String type;

    ResponseHeaderType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
