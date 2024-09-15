package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class Http11Request implements HttpRequest {

    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int HEADER_INDEX_SIZE = 2;
    private static final SessionManager sessionManager = SessionManager.getInstance();

    private final Http11RequestStartLine startLine;
    private final Http11RequestHeader headers;
    private final HttpRequestBody body;
    private final Session session;

    private Http11Request(Http11RequestStartLine startLine, Http11RequestHeader headers, Session session) {
        this(startLine, headers, null, session);
    }

    private Http11Request(
            Http11RequestStartLine startLine,
            Http11RequestHeader headers,
            HttpRequestBody body,
            Session session
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.session = session;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Http11RequestStartLine startLine = Http11RequestStartLine.from(bufferedReader.readLine());
        Http11RequestHeader httpHeaders = Http11RequestHeader.from(createHttpHeaderMap(bufferedReader));
        Session session = getOrCreateSession(httpHeaders);

        if (startLine.getMethod().hasBody()) {
            HttpRequestBody body = HttpRequestBody.create(createBody(bufferedReader, httpHeaders.getContentLength()));
            return new Http11Request(startLine, httpHeaders, body, session);
        }
        return new Http11Request(startLine, httpHeaders, session);
    }

    private static Session getOrCreateSession(Http11RequestHeader httpHeaders) {
        return httpHeaders.getCookieValue(Session.SESSION_COOKIE_KEY)
                .map(Http11Request::getOrCreateSession)
                .orElseGet(Http11Request::createSession);
    }

    private static Session getOrCreateSession(String sessionId) {
        try {
            return sessionManager.findSession(sessionId);
        } catch (IllegalArgumentException e) {
            return createSession();
        }
    }

    private static Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }

    private static Map<String, List<String>> createHttpHeaderMap(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_KEY_VALUE_DELIMITER))
                .filter(parts -> parts.length == HEADER_INDEX_SIZE)
                .collect(groupingBy(
                        parts -> parts[HEADER_KEY_INDEX].trim(),
                        mapping(parts -> parts[HEADER_VALUE_INDEX].trim(), toList())
                ));
    }

    private static String createBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    @Override
    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    @Override
    public String getEndpoint() {
        return startLine.getEndPoint();
    }

    @Override
    public Http11RequestStartLine getStartLine() {
        return startLine;
    }

    @Override
    public Http11RequestHeader getHeaders() {
        return headers;
    }

    @Override
    public String getBodyValue() {
        return body.getValue();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getQueryParamFromUrl(String param) {
        return startLine.getRequestTarget().getParam(param);
    }

    @Override
    public String getQueryParamFromBody(String param) {
        return body.getQueryParam(param);
    }

    @Override
    public String toString() {
        return "Http11Request{" +
               "startLine=" + startLine +
               ", headers=" + headers +
               ", body='" + body + '\'' +
               '}';
    }
}
