package servlet.http.request;

import servlet.http.request.uri.URI;
import servlet.http.HttpMethod;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int VALID_SPLIT_REQUEST_LINE_LENGTH = 3;

    private final HttpMethod httpMethod;

    private final URI uri;

    private final String httpVersion;

    protected RequestLine(String requestLine) {
        validateRequestLine(requestLine);
        String[] requestLines = split(requestLine);
        this.httpMethod = HttpMethod.from(requestLines[0]);
        this.uri = new URI(requestLines[1]);
        this.httpVersion = requestLines[2];
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line은 필수입니다.");
        }
    }

    private String[] split(String requestLine) {
        String[] requestLines = requestLine.split(DELIMITER);
        if (requestLines.length != VALID_SPLIT_REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Request line입니다.");
        }
        return requestLines;
    }

    protected HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    protected String getPath() {
        return uri.getPath();
    }

    protected String getQueryParamValue(String key) {
        return uri.getQueryParamValue(key);
    }

    protected String getHttpVersion() {
        return this.httpVersion;
    }

    protected boolean existQueryParams() {
        return uri.existQueryParams();
    }
}
