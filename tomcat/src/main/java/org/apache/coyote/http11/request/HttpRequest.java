package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;

public final class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getMethodType() {
        return requestLine.getMethod();
    }

    public boolean isResource() {
        return requestLine.isCss() || requestLine.isJs() || requestLine.isHtml();
    }

    public String getUrl() {
        return requestLine.getRequestUrl();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }
}
