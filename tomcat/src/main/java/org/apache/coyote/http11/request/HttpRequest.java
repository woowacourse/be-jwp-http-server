package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String requestBody;

    public HttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = extractRequestLine(reader);
        this.headers = extractHeaders(reader);
        this.requestBody = extractRequestBody(reader);

        log.info("request header: {}", headers);
    }

    private RequestLine extractRequestLine(BufferedReader reader) throws IOException {
        String method = "";
        String path = "";
        String queryString = "";
        String protocol = "";

        String firstLine = reader.readLine();
        String[] requestLineParts = firstLine.split(" ");

        method = requestLineParts[0];
        String uri = requestLineParts[1];
        protocol = requestLineParts[2];

        int index = uri.indexOf("?");
        if (index != -1) {
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        } else {
            path = uri;
            queryString = null;
        }

        return new RequestLine(method, path, queryString, protocol);
    }

    private Map<String, String> extractHeaders(BufferedReader reader) throws IOException {
        Map<String, String> RequestHeaders = new HashMap<>();
        while (true) {
            String readLine = reader.readLine();
            if (readLine == null || readLine.isEmpty()) {
                break;
            }

            String[] headerLineParts = readLine.split(": ");
            RequestHeaders.put(headerLineParts[0], headerLineParts[1]);
        }

        return RequestHeaders;
    }

    public String extractRequestBody(BufferedReader reader) throws IOException {
        try {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
