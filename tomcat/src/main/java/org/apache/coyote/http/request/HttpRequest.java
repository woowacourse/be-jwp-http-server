package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;

import java.util.List;

import static org.apache.coyote.http.Constants.CRLF;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Header headers;
    private String body;

    public static HttpRequest of(String request) {
        List<String> requests = List.of(request.split(CRLF));
        return new HttpRequest(RequestLine.of(requests.getFirst()), Header.of(requests.subList(1, requests.size())));
    }

    public static HttpRequest of(List<String> requests) {
        return new HttpRequest(RequestLine.of(requests.getFirst()), Header.of(requests.subList(1, requests.size())));
    }

    private HttpRequest(RequestLine requestLine, Header headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Header getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
