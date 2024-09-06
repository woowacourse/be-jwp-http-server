package org.apache.coyote.http11;

import java.util.StringJoiner;
import org.apache.coyote.http11.body.HttpResponseBody;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.startline.HttpResponseLine;
import org.apache.coyote.http11.startline.HttpStatus;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final HttpResponseLine httpResponseLine;
    private final HttpHeaders httpHeaders;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(String httpVersion) {
        this.httpResponseLine = new HttpResponseLine(httpVersion);
        this.httpHeaders = new HttpHeaders();
        this.httpResponseBody = new HttpResponseBody();
    }

    public byte[] getBytes() {
        return stringify().getBytes();
    }

    private String stringify() {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add(httpResponseLine.stringify());
        joiner.add(httpHeaders.stringify(DELIMITER));
        joiner.add("");
        joiner.add(httpResponseBody.getValue());

        return joiner.toString();
    }

    public void addHeader(HttpHeader header, String value) {
        httpHeaders.add(header, value);
    }

    public void setStatus(HttpStatus httpStatus) {
        httpResponseLine.setStatus(httpStatus);
    }

    public void setBody(String body) {
        httpResponseBody.setValue(body);
        httpHeaders.add(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void addSessionToCookies(String session) {
        httpHeaders.addSessionToCookies(session);
    }

    public boolean isValid() {
        return httpResponseLine.isValid() && httpHeaders.contains(HttpHeader.CONTENT_LENGTH);
    }
}
