package org.apache.coyote.http11.domain.request;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;

    public HttpRequest(String requestLine) throws IOException {
        validateLineEmpty(requestLine);
        this.requestLine = new RequestLine(requestLine);
    }

    private void validateLineEmpty(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new IllegalArgumentException("Line is Empty");
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParameter(String key) {
        return requestLine.getQueryParameter(key);
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}

