package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseHeader {

    private static final String HEADER_FORMAT = "%s: %s ";
    private static final CharSequence HTTP_DELIMITER = "\r\n";
    List<String> headers;

    public HttpResponseHeader() {
        headers = new ArrayList<>();
    }

    public void addHeader(String name, String value) {
        headers.add(HEADER_FORMAT.formatted(name, value));
    }

    public String buildOutput() {
        return String.join(HTTP_DELIMITER, headers);
    }
}