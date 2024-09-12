package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private Map<String, String> queryString;
    private String protocolVersion;
    private Map<String, String> headers;
    private HttpCookie httpCookie;
    private Session session;
    private String body;

    public HttpRequest() {
    }

    public boolean hasMethod(HttpMethod method) {
        return this.method == method;
    }

    public boolean hasPath(String path) {
        return this.path.equals(path);
    }

    public boolean hasQueryString() {
        return !queryString.isEmpty();
    }

    public boolean hasCookieFrom(String input) {
        return httpCookie.hasValue(input);
    }

    public String getQueryData(String input) {
        return queryString.get(input);
    }

    public String getHeaderData(String input) {
        return headers.get(input);
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setQueryString(Map<String, String> queryString) {
        this.queryString = queryString;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHttpCookie(HttpCookie cookie) {
        this.httpCookie = cookie;
    }
}
