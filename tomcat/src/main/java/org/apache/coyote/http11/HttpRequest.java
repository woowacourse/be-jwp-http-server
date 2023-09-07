package org.apache.coyote.http11;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final RequestHeader requestHeader;
    private final QueryString queryString;
    private final RequestBody requestBody;
    private final Session session;

    private HttpRequest(final RequestHeader requestHeader, final RequestBody requestBody, final QueryString queryString,
                        final Session session) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.queryString = queryString;
        this.session = session;
    }

    public static HttpRequest of(final List<String> requestHeaderStrings, final String requestBodyString) {
        final RequestHeader requestHeader = RequestHeader.from(requestHeaderStrings);
        final RequestBody requestBody = RequestBody.from(requestBodyString);
        final QueryString queryString = QueryString.from(requestHeader.getOriginRequestURI());
        final Session session = findSessionId(requestHeader);
        return new HttpRequest(requestHeader, requestBody, queryString, session);
    }

    private static Session findSessionId(final RequestHeader requestHeader) {
        final String sessionId = requestHeader.getCookie("JSESSIONID");
        return SESSION_MANAGER.getSessionId(sessionId);
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.requestHeader.isSameHttpMethod(httpMethod);
    }

    public String getParsedRequestURI() {
        return requestHeader.getParsedRequestURI();
    }

    public String getOriginRequestURI() {
        return requestHeader.getOriginRequestURI();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpVersion getHttpVersion() {
        return requestHeader.getHttpVersion();
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return session.getId();
    }

    public String getCookie(final String key) {
        return requestHeader.getCookie(key);
    }

    public boolean isNotSameSessionId() {
        final String cookieSessionId = requestHeader.getCookie("JSESSIONID");
        final String requestSessionId = session.getId();

        return Objects.isNull(cookieSessionId) || !cookieSessionId.equals(requestSessionId);
    }
}
