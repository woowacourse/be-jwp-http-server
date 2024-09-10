package org.apache.coyote.request;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.response.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean pointsTo(HttpMethod httpMethod, String path) {
        return httpMethod == requestLine.getMethod()
               && path.equals(requestLine.getPath());
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpCookie getCookie() {
        return new HttpCookie(httpHeader.get(HttpHeaders.COOKIE.getName()));
    }
}
