package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestBody;
import org.apache.coyote.http11.httprequest.HttpRequestHeader;
import org.apache.coyote.http11.httprequest.HttpRequestLine;

public class HttpRequestConvertor {

    private static final String HEADER_DELIMITER = ":";

    public static HttpRequest convertHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("요청이 비어 있습니다.");
        }

        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);
        Map<String, String> headers = getHeaders(bufferedReader);
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(headers);

        if (httpRequestHeader.containsHeader(HttpHeaderName.CONTENT_LENGTH)
                && httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_TYPE).equals("application/x-www-form-urlencoded")
        ) {
            HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader);
            return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
        }

        return new HttpRequest(httpRequestLine, httpRequestHeader);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(HEADER_DELIMITER);
            headers.put(requestLine[0], parseHeaderValue(requestLine));
        }

        return headers;
    }

    private static String parseHeaderValue(String[] requestLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < requestLine.length; i++) {
            sb.append(requestLine[i].strip());
        }

        return sb.toString();
    }

    private static HttpRequestBody getHttpRequestBody(
            BufferedReader bufferedReader,
            HttpRequestHeader httpRequestHeader
    ) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getHeaderValue(HttpHeaderName.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        Map<String, String> body = extractBody(requestBody);
        return new HttpRequestBody(body);
    }

    private static Map<String, String> extractBody(String requestBody) {
        Map<String, String> body = new HashMap<>();
        String[] tokens = requestBody.split("&");
        for (String token : tokens) {
            String key = token.split("=")[0];
            String value = token.split("=")[1];
            body.put(key, value);
        }
        return body;
    }
}
