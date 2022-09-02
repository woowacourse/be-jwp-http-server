package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpStatusCode statusCode;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpResponse(final HttpStatusCode statusCode, final HttpHeaders httpHeaders, final String body) {
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public byte[] getBytes() {
        final String join = String.join("\r\n",
                "HTTP/1.1 " + statusCode.getValue() + " ",
                httpHeaders.encodingToString(),
                "",
                body);
        return join.getBytes();
    }

    public static class Builder {

        private HttpStatusCode statusCode;
        private HttpHeaders httpHeaders;
        private String body;

        public Builder() {
        }

        public Builder statusCode(final HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder headers(final HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, httpHeaders, body);
        }
    }
}
