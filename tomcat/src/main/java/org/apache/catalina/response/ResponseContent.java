package org.apache.catalina.response;

public class ResponseContent {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_CHARSET = "charset=utf-8";
    private static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length: ";

    private final HttpStatus httpStatus;
    private final String contentType;
    private final int contentLength;
    private final String body;

    public ResponseContent(HttpStatus httpStatus, String contentType, String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = body.getBytes().length;
        this.body = body;
    }

    public String responseToString() {
        return String.join("\r\n",
                HTTP_VERSION + " " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                HEADER_CONTENT_TYPE + contentType + ";" + DEFAULT_CHARSET + " ",
                HEADER_CONTENT_LENGTH + contentLength + " ",
                "",
                body);
    }
}