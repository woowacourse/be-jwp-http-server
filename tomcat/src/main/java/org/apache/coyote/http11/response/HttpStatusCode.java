package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK")
    ;

    private final int code;
    private final String type;

    HttpStatusCode(final int code, final String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%d %s", code, type);
    }
}
