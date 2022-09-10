package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestParams requestParams;

    public HttpRequest(final RequestLine requestLine,
                       final HttpHeaders headers,
                       final RequestParams requestParams) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestParams = requestParams;
    }

    public static HttpRequest from(final String firstLine, final List<String> headers, final String requestBody) {
        final RequestLine requestLine = RequestLine.from(firstLine);
        final HttpHeaders httpHeaders = HttpHeaders.from(headers);
        final RequestParams requestParams = createRequestParams(requestLine, requestBody, httpHeaders);

        return new HttpRequest(requestLine, httpHeaders, requestParams);
    }

    private static RequestParams createRequestParams(final RequestLine requestLine,
                                                     final String requestBody,
                                                     final HttpHeaders httpHeaders) {
        if (isFormUrlEncoded(requestLine.getMethod(), httpHeaders)) {
            return RequestParams.fromUrlEncoded(requestBody);
        }

        return RequestParams.from(requestLine.getQueryParams());
    }

    private static boolean isFormUrlEncoded(final HttpMethod httpMethod, final HttpHeaders httpHeaders) {
        return httpMethod == HttpMethod.POST
                && getContentType(httpHeaders).equals(ContentType.APPLICATION_X_WWW_FORM_URLENCODED.getValue());
    }

    private static String getContentType(final HttpHeaders httpHeaders) {
        return httpHeaders.findOneHeaderByKey(HttpHeaders.CONTENT_TYPE)
                .orElseThrow(() -> new IllegalArgumentException("필수 헤더가 들어오지 않았습니다." + HttpHeaders.CONTENT_TYPE))
                .getValue();
    }

    public boolean hasSession() {
        return headers.hasSession();
    }

    public Optional<Session> getSession() {
        final Optional<String> sessionId = headers.getCookies()
                .getSessionId();
        if (sessionId.isEmpty()) {
            return Optional.empty();
        }

        return SessionManager.getInstance()
                .findSession(sessionId.orElseThrow());
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public QueryParams getQueryParams() {
        return requestLine.getQueryParams();
    }

    public String getUriPath() {
        return requestLine.getUriPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Cookies getCookies() {
        return headers.getCookies();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpRequest)) {
            return false;
        }
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(getRequestLine(), that.getRequestLine()) && Objects.equals(getHeaders(),
                that.getHeaders()) && Objects.equals(getRequestParams(), that.getRequestParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestLine(), getHeaders(), getRequestParams());
    }
}
