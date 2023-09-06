package org.apache.coyote.http11.common;

public enum HttpStatus {
    OK(200, "OK "),
    NOT_FOUND(404, "Not Found "),
    FOUND(302, "Found ");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
