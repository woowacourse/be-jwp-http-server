package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String REQUEST_HEADER_SUFFIX = "";

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.startLine = readStartLine(bufferedReader);
        this.header = readHeader(bufferedReader);
        this.body = readBody(bufferedReader);
    }

    private HttpRequestStartLine readStartLine(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("요청의 시작줄이 비어있습니다.");
        }
        return new HttpRequestStartLine(startLine);
    }

    private HttpRequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!REQUEST_HEADER_SUFFIX.equals(line)) {
            line = bufferedReader.readLine();
            lines.add(line);
        }
        return new HttpRequestHeader(lines);
    }

    private HttpRequestBody readBody(final BufferedReader bufferedReader) throws IOException {
        final int contentLength = header.getContentLength();
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String bodyLine = new String(buffer);
        return new HttpRequestBody(bodyLine);
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }
}
