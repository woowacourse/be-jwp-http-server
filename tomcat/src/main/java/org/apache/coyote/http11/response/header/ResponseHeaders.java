package org.apache.coyote.http11.response.header;

import static org.apache.coyote.http11.Constants.CRLF;
import static org.apache.coyote.http11.Constants.HTTP_HEADER_SEPARATOR;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;

public class ResponseHeaders {

    private final LinkedHashMap<HttpHeader, String> headers;

    public ResponseHeaders(LinkedHashMap<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(HttpHeader name, String value) {
        headers.put(name, value);
    }

    public String toMessage() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<HttpHeader, String> entry : headers.entrySet()) {
            builder.append(entry.getKey().getName())
                    .append(HTTP_HEADER_SEPARATOR)
                    .append(entry.getValue())
                    .append(CRLF);
        }
        return builder.toString();
    }
}
