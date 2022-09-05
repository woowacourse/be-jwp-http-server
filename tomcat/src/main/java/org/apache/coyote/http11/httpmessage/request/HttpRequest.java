package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        Headers headers = Headers.of(getHeaders(bufferedReader));
        RequestBody requestBody = new RequestBody(getBody(bufferedReader, headers));

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> getHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private static String getBody(BufferedReader bufferedReader, Headers headers) throws IOException {
        String contentLengthValue = headers.getHeader("Content-Length");
        if (contentLengthValue == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean matchRequestLine(HttpMethod httpMethod, Pattern uriPattern) {
        return requestLine.matchHttpMethod(httpMethod) && requestLine.matchUri(uriPattern);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Object getParameter(String key) {
        return requestLine.getParameter(key);
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(headers,
                that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, headers);
    }
}
