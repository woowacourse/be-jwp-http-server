package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RequestBody {
    public static final RequestBody EMPTY = new RequestBody(Collections.emptyMap());

    private final Map<String, String> requestBody;

    private RequestBody(final Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    private static RequestBody from(final String requestBody) {
        final Map<String, String> result = new HashMap<>();
        int index = requestBody.indexOf("?");
        if (index != -1) {
            final StringTokenizer token = new StringTokenizer(requestBody.substring(index + 1), "&");
            while (token.hasMoreTokens()) {
                final String param = token.nextToken();
                final String[] split = param.split("=");
                result.put(split[0], split[1]);
            }
        }
        return new RequestBody(result);
    }

    public static RequestBody of(final RequestHeader requestHeader, final BufferedReader bufferedReader) throws IOException {
        if (!requestHeader.hasBody()) {
            return RequestBody.EMPTY;
        }
        int contentLength = Integer.parseInt(requestHeader.getHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String body = new String(buffer);

        return RequestBody.from(body);
    }
}
