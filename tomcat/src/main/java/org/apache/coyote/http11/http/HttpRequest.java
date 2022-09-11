package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.util.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final String requestBody;
    private final Cookies cookies;

    private HttpRequest(final RequestLine requestLine, final HttpRequestHeaders httpHeaders,
                        final String requestBody, final Cookies cookies) {
        this.requestLine = requestLine;
        this.httpRequestHeaders = httpHeaders;
        this.requestBody = requestBody;
        this.cookies = cookies;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = generateRequestLine(bufferedReader);
        final var httpHeaders = generateHttpHeader(bufferedReader);
        final var requestBody = generateHttpRequestBody(httpHeaders, bufferedReader);
        final var cookies = httpHeaders.getCookies();
        return new HttpRequest(requestLine, httpHeaders, requestBody, cookies);
    }

    private static RequestLine generateRequestLine(final BufferedReader bufferedReader) throws IOException {
        return RequestLine.from(bufferedReader.readLine());
    }

    private static HttpRequestHeaders generateHttpHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpHeaders = new ArrayList<>();
        var line = bufferedReader.readLine();

        if (Objects.isNull(line)) {
            return HttpRequestHeaders.from(httpHeaders);
        }

        while (!"".equals(line)) {
            httpHeaders.add(line);
            line = bufferedReader.readLine();
        }
        return HttpRequestHeaders.from(httpHeaders);
    }

    private static String generateHttpRequestBody(final HttpRequestHeaders httpHeaders,
                                                  final BufferedReader bufferedReader)
            throws IOException {
        final var contentLength = httpHeaders.getContentLength();
        final var length = contentLength.getContentLength();
        final char[] body = new char[length];

        bufferedReader.read(body);

        return new String(body);
    }

    public boolean containUrl(final String url) {
        return requestLine.containUrl(url);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Session getSession(final boolean bool) {
        if (Objects.isNull(cookies)) {
            return getSessionWithoutCookie(bool);
        }

        final Optional<String> jSessionId = cookies.getJSessionId();
        if (bool) {
            return jSessionId.map(Session::new).orElseGet(() -> new Session(UUID.randomUUID().toString()));
        }
        return jSessionId.map(Session::new).orElseGet(() -> null);
    }

    private Session getSessionWithoutCookie(final boolean bool) {
        if (bool) {
            return new Session(UUID.randomUUID().toString());
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }
}
