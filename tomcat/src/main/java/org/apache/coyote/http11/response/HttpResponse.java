package org.apache.coyote.http11.response;

import java.util.StringJoiner;
import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.HttpHeader;
import org.apache.coyote.http11.common.HttpStatusCode;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";

    private final ResponseLine responseLine;
    private final HttpHeader headers;
    private ResponseBody responseBody;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, HttpHeader.empty(), ResponseBody.empty());
    }

    public HttpResponse(HttpStatusCode statusCode, HttpHeader headers, ResponseBody responseBody) {
        this.responseLine = new ResponseLine(VERSION, statusCode);
        this.headers = headers;
        this.responseBody = responseBody;
        headers.setContentLength(responseBody.size());
    }

    public HttpResponse addHeader(String key, String value) {
        headers.add(key, value);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        headers.setContentType(contentType);
        return this;
    }

    public HttpResponse addCookie(String key, String value) {
        if (headers.headers().containsKey("Set-Cookie")) {
            headers.headers().put("Set-Cookie", headers.headers().get("Set-Cookie") + "; " + key + "=" + value);
            return this;
        }
        headers.headers().put("Set-Cookie", key + "=" + value);
        return this;
    }

    public HttpResponse setBody(String rawBody) {
        responseBody = new ResponseBody(rawBody.getBytes());
        headers.setContentLength(responseBody.size());
        return this;
    }

    public HttpResponse setStatus(HttpStatusCode statusCode) {
        responseLine.setStatusCode(statusCode);
        return this;
    }

    public HttpResponse sendRedirect(String location) {
        setStatus(HttpStatusCode.FOUND);
        addHeader("Location", location);
        return this;
    }

    public byte[] getAsBytes() {
        return toHttpMessage().getBytes();
    }

    public String toHttpMessage() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        joiner.add(responseLine.toResponseString())
                .add(headers.toString())
                .add("")
                .add(new String(responseBody.getBody()));
        return joiner.toString();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "responseLine=" + responseLine +
                ", header=" + headers +
                ", body=" + responseBody +
                '}';
    }
}
