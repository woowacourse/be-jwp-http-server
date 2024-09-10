package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.httpmessage.HttpCookie;
import org.apache.coyote.http11.httpmessage.HttpHeaders;

public class Request {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpCookie cookies;
    private final String body;

    private Request(RequestLine requestLine, HttpHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        if (headers.contains("Cookie")) {
            this.cookies = HttpCookie.parseFrom(headers.get("Cookie"));
        } else {
            this.cookies = new HttpCookie();
        }
        this.body = body;
    }

    public static Request readFrom(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.parseFrom(reader.readLine());
        HttpHeaders header = new HttpHeaders(readHeader(reader));
        String requestBody = readBody(reader, header.getContentLength());
        return new Request(requestLine, header, requestBody);
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while (true) {
            line = reader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] headerLine = line.split(": ");
            headers.put(headerLine[0], headerLine[1]);
        }
        return headers;
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isStaticResourceRequest() {
        return requestLine.isStaticResourceRequest();
    }

    public Method getMethod() {
        return requestLine.method();
    }

    public String getTarget() {
        return requestLine.target();
    }

    public String getHttpVersion() {
        return requestLine.httpVersion();
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
