package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http11.HttpVersion;

public class HttpRequestLine {

    private static final String SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final Uri uri;
    private final HttpVersion httpVersion;

    public static HttpRequestLine from(final String requestLine) {
        final List<String> requestLineInfos = Arrays.asList(requestLine.split(SEPARATOR));
        return new HttpRequestLine(requestLineInfos.get(0), requestLineInfos.get(1), requestLineInfos.get(2));
    }

    private HttpRequestLine(
            final String httpMethod,
            final String uri,
            final String httpVersion
    ) {
        this.httpMethod = HttpMethod.valueOf(httpMethod);
        this.uri = Uri.from(uri);
        this.httpVersion = HttpVersion.findVersion(httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Uri getUri() {
        return uri;
    }
}
