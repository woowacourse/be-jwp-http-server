package org.apache.coyote.http11.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequestConvertor {

    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_KEY_INDEX = 0;
    private static final String BODY_DELIMITER = "&";
    private static final int BODY_TUPLE_MIN_LENGTH = 2;
    private static final String TUPLE_DELIMITER = "=";
    private static final int TUPLE_KEY_INDEX = 0;
    private static final int TUPLE_VALUE_INDEX = 1;
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    public static HttpRequest convertHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(getHeaders(bufferedReader));
        Session session = getOrCreateSession(httpRequestHeader);

        if (isExistRequestBody(httpRequestHeader)) {
            HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader);
            return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody, session);
        }

        return new HttpRequest(httpRequestLine, httpRequestHeader, session);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] requestLine = line.split(HEADER_DELIMITER);
            headers.put(requestLine[HEADER_KEY_INDEX], parseHeaderValue(requestLine));
        }

        return headers;
    }

    private static String parseHeaderValue(String[] requestLine) {
        return String.join(HEADER_DELIMITER, Arrays.copyOfRange(requestLine, 1, requestLine.length)).strip();
    }

    private static Session getOrCreateSession(HttpRequestHeader httpRequestHeader) {
        if (!httpRequestHeader.containsHeader(HttpHeaderName.COOKIE)) {
            return createSession();
        }

        HttpCookie httpCookie = HttpCookieConvertor.convertHttpCookie(
                httpRequestHeader.getHeaderValue(HttpHeaderName.COOKIE));
        if (!httpCookie.containsSession()) {
            return createSession();
        }

        return getOrCreateSession(httpCookie);
    }

    private static Session getOrCreateSession(HttpCookie httpCookie) {
        String sessionId = httpCookie.getSessionId();
        if (!SESSION_MANAGER.containsSession(sessionId)) {
            return createSession();
        }
        return SESSION_MANAGER.findSession(sessionId);
    }

    private static Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        SESSION_MANAGER.add(session);
        return session;
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
        String[] tokens = requestBody.split(BODY_DELIMITER);
        return Arrays.stream(tokens)
                .filter(token -> token.split(TUPLE_DELIMITER).length >= BODY_TUPLE_MIN_LENGTH)
                .map(token -> token.split(TUPLE_DELIMITER))
                .collect(Collectors.toMap(
                        token -> token[TUPLE_KEY_INDEX],
                        token -> token[TUPLE_VALUE_INDEX]
                ));
    }

    private static boolean isExistRequestBody(HttpRequestHeader httpRequestHeader) {
        return httpRequestHeader.containsHeader(HttpHeaderName.CONTENT_LENGTH) && httpRequestHeader.existRequestBody();
    }
}
