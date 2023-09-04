package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private final Map<String, String> headers;

    private RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(BufferedReader httpRequestHeader) throws IOException {
        Map<String, String> headerValues = new HashMap<>();
        String line = httpRequestHeader.readLine();
        while (!line.equals("")) {
            String[] header = line.split(":");
            headerValues.put(header[0], header[1]);
            line = httpRequestHeader.readLine();
        }
        return new RequestHeader(headerValues);
    }

    public Cookie parseCookie() {
        if (headers.containsKey(COOKIE)) {
            return Cookie.from(headers.get(COOKIE));
        }
        return Cookie.from("");
    }

    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH)) {
            String contentLengthValue = headers.get(CONTENT_LENGTH).trim();
            return Integer.parseInt(contentLengthValue);
        }
        return 0;
    }
}
