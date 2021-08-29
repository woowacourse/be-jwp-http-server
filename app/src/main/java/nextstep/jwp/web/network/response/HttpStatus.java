package nextstep.jwp.web.network.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED");

    private final int code;
    private final String name;

    HttpStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int toCode() {
        return code;
    }

    public String toReasonPhrase() {
        return name;
    }
}
