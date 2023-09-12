package org.apache.coyote.http11.request;

import java.util.Objects;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String httpMethod;
    private final Path path;
    private final String httpVersion;

    private RequestLine(String httpMethod, Path path, String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] splitRequestLine = Objects.requireNonNull(requestLine.trim()).split(REQUEST_LINE_DELIMITER);
        String httpMethod = splitRequestLine[0];
        Path path = Path.from(splitRequestLine[1]);
        String httpVersion = splitRequestLine[2];
        return new RequestLine(httpMethod, path, httpVersion);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path.getPath();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
