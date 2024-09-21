package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.HttpRequestMethod;

public class RequestLine {

    private final HttpRequestMethod method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(String rawRequestLine) {
        String[] parts = rawRequestLine.split(" ", 3);
        this.method = HttpRequestMethod.valueOf(parts[0]);
        this.path = parts[1];
        this.protocolVersion = parts[2];
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String render() {
        return method + " " + path + " " + protocolVersion;
    }
}
