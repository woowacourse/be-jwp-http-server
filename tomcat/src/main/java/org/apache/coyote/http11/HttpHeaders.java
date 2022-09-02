package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(final BufferedReader bufferedReader) throws IOException {
        this.headers = extractHeaders(bufferedReader);
    }

    private Map<String, String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (!"".equals(line)) {
                final String[] headerValue = line.split(": ");
                final String header = headerValue[0];
                final String value = headerValue[1];
                headers.put(header, value);
            }
        }
        return headers;
    }

    public HttpHeaders addHeader(final String header, final String value) {
        headers.put(header, value);
        return this;
    }

    public HttpHeaders addHeader(final String header, final int value) {
        headers.put(header, String.valueOf(value));
        return this;
    }

    public String encodingToString() {
        final List<String> headers = new ArrayList<>();
        for (Entry<String, String> entry : this.headers.entrySet()) {
            final String join = String.join(": ", entry.getKey(), entry.getValue()) + " ";
            headers.add(join);
        }
        return String.join("\r\n", headers);
    }
}
