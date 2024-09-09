package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATUS_LINE_FORMAT = "%s %d %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String REDIRECTION_HEADER = "Location";
    private static final String COOKIE_HEADER = "Set-Cookie";

    private final Protocol protocol = Protocol.HTTP11;
    private HttpStatus httpStatus;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this(null, new HashMap<>(), null);
    }

    public HttpResponse(HttpStatus httpStatus) {
        this(httpStatus, new HashMap<>(), null);
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers) {
        this(httpStatus, headers, "");
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createRedirectResponse(HttpStatus httpStatus, String location) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(
                httpStatus,
                headers
        );
    }

    public static HttpResponse createTextResponse(HttpStatus httpStatus, String responseBody) {
        Map<String, String> headers = new HashMap<>();
        int contentLength = responseBody.getBytes().length;
        headers.put("Content-Type", "text/plain;charset=utf-8 ");
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponse(
                httpStatus,
                headers,
                responseBody
        );
    }

    public static HttpResponse createFileResponse(ResponseFile responseFile) {
        Map<String, String> headers = new HashMap<>();
        String responseBody = responseFile.getContent();
        int contentLength = responseBody.getBytes().length;
        headers.put("Content-Type", responseFile.getContentType());
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponse(
                HttpStatus.OK,
                headers,
                responseBody
        );
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addRedirectHeader(String location) {
        headers.put(REDIRECTION_HEADER, location);
    }

    public void addCookie(ResponseCookie cookie) {
        headers.put(COOKIE_HEADER, cookie.toResponse());
    }

    public String toResponse() {
        String statusLine = getStatusLine();
        String headers = getHeaders();

        return String.join(
                LINE_SEPARATOR,
                statusLine,
                headers,
                "",
                body
        );
    }

    private String getStatusLine() {
        return String.format(STATUS_LINE_FORMAT,
                protocol.getName(),
                httpStatus.getCode(),
                httpStatus.getReasonPhrase()
        );
    }

    private String getHeaders() {
        List<String> formattedHeaders = new ArrayList<>();
        for (String headerKey : headers.keySet()) {
            String headerValue = headers.get(headerKey);
            String formattedHeader = String.format(HEADER_FORMAT, headerKey, headerValue);
            formattedHeaders.add(formattedHeader);
        }
        return String.join(LINE_SEPARATOR, formattedHeaders);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addFile(ResponseFile responseFile) {
        headers.put("Content-Type", responseFile.getContentType());
        headers.put("Content-Length", String.valueOf(responseFile.getContentLength()));
        body = responseFile.getContent();
    }
}
