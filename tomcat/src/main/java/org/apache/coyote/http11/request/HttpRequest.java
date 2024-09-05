package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(BufferedReader requestReader) throws IOException {
        List<String> requestHead = readRequestHead(requestReader);
        validateRequestHead(requestHead);
        RequestLine requestLine = getRequestLine(requestHead);
        HttpHeaders headers = getHeaders(requestHead);

        int contentLength = headers.getAsInt("Content-Length").orElse(0);
        String body = readBody(contentLength, requestReader);

        return new HttpRequest(requestLine, headers, body);
    }

    private static List<String> readRequestHead(BufferedReader requestReader) throws IOException {
        List<String> requestHead = new ArrayList<>();
        String line;
        while ((line = requestReader.readLine()) != null && !line.isEmpty()) {
            requestHead.add(line);
        }
        return requestHead;
    }

    private static void validateRequestHead(List<String> requestLines) {
        if (requestLines.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 형식입니다.");
        }
    }

    private static RequestLine getRequestLine(List<String> requestHead) {
        String firstLine = requestHead.getFirst();
        return RequestLine.of(firstLine);
    }

    private static HttpHeaders getHeaders(List<String> requestHead) {
        List<String> headers = new ArrayList<>(requestHead.subList(1, requestHead.size()));
        return HttpHeaders.of(headers);
    }

    private static String readBody(int contentLength, BufferedReader requestReader) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] body = new char[contentLength];
        requestReader.read(body, 0, contentLength);
        return new String(body);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Queries getQueries() {
        return requestLine.getQueries();
    }

    public boolean isQueriesEmpty() {
        return getQueries().isEmpty();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getBody() {
        return body;
    }

    public Session getSession() {
        RequestCookies requestCookies = RequestCookies.of(headers.get("Cookie"));
        String sessionId = requestCookies.get("JSESSIONID");
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) {
            return Session.create();
        }
        return session;
    }
}
