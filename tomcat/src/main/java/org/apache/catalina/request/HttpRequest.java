package org.apache.catalina.request;

import java.util.List;
import org.apache.catalina.http.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(List<String> headerLines, String bodyLine) {
        this.requestLine = new RequestLine(headerLines.getFirst());
        this.header = new RequestHeader(headerLines.subList(1, headerLines.size()));
        this.body = mapBody(bodyLine);
    }

    private RequestBody mapBody(String bodyLine) {
        if (requestLine.isMethod(HttpMethod.POST)) {
            return new RequestBody(bodyLine);
        }
        return new RequestBody();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(String paramName) {
        return requestLine.getQueryParam(paramName);
    }

    public String getContentType() {
        return requestLine.getContentType();
    }

    public String getSessionId() {
        return header.getSessionId();
    }

    public String getHttpCookie() {
        return header.getHttpCookie();
    }

    public boolean hasCookie() {
        return header.hasCookie();
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return requestLine.isMethod(httpMethod);
    }

    public boolean isStaticRequest() {
        return requestLine.isStaticRequest();
    }

    public boolean hasSession() {
        return header.hasSession();
    }

    public String getBodyParam(String parameter) {
        return body.get(parameter);
    }

    public RequestBody getBody() {
        return body;
    }
}
