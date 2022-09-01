package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final ContentType contentType;
    private final HttpStatus status;
    private final HttpHeaders unnecessaryHeaders;
    private final String responseBody;

    public HttpResponse(final ContentType contentType,
                        final HttpStatus status,
                        final HttpHeaders unnecessaryHeaders,
                        final String responseBody) {
        this.contentType = contentType;
        this.status = status;
        this.unnecessaryHeaders = unnecessaryHeaders;
        this.responseBody = responseBody;
    }

    public String toHttpMessage() {
        final String headerMessage = unnecessaryHeaders.toHttpMessageHeader();
        if (headerMessage.equals("")) {
            return toNoContainUnnecessaryHeaderHttpMessage();
        }
        return String.join("\r\n",
                String.format("HTTP/1.1 %d %s ", status.getStatusCode(), status.getStatusName()),
                String.format("Content-Type: %s;charset=utf-8 ", contentType.getValue()),
                "Content-Length: " + responseBody.getBytes().length + " ",
                headerMessage,
                "",
                responseBody);
    }

    private String toNoContainUnnecessaryHeaderHttpMessage() {
        return String.join("\r\n",
                String.format("HTTP/1.1 %d %s ", status.getStatusCode(), status.getStatusName()),
                String.format("Content-Type: %s;charset=utf-8 ", contentType.getValue()),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
