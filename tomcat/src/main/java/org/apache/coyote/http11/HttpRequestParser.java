package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.Body;
import org.apache.coyote.http11.request.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader reader) {
        StartLine startLine = StartLine.from(readLine(reader));
        List<String> headerLines = getHeaderLines(reader);
        Headers headers = Headers.from(headerLines);
        Body body = parseBody(reader, contentLength(headers));
        return HttpRequest.builder()
                .startLine(startLine)
                .headers(headers)
                .body(body)
                .build();
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> getHeaderLines(BufferedReader reader) {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = readLine(reader)) != null) {
            if (line.isEmpty()) {
                break;
            }
            headers.add(line);
        }
        return headers;
    }

    private Integer contentLength(Headers headers) {
        if (!headers.contains("Content-Length")) {
            return null;
        }
        return Integer.parseInt(headers.get("Content-Length"));
    }

    private Body parseBody(BufferedReader reader, Integer length) {
        if (length == null) {
            return new Body(null);
        }
        char[] chars = new char[length];
        try {
            reader.read(chars);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Body(new String(chars));
    }
}
