package org.apache.coyote.http11.request;

public class HttpRequestBody {
    private final String body;

    public HttpRequestBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
