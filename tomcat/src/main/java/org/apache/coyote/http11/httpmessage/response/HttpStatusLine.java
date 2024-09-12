package org.apache.coyote.http11.httpmessage.response;

public class HttpStatusLine {

    private final String protocolVersion;
    private final int statusCode;
    private final String statusText;

    public HttpStatusLine(String protocolVersion, int statusCode, String statusText) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String toHttpMessage() {
        return String.format("%s %s %s ", protocolVersion, statusCode, statusText);
    }
}
