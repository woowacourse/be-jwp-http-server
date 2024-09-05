package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.util.FileReader;

public class HttpResponse {

    private static final FileReader FILE_READER = FileReader.getInstance();

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie";

    private final HttpStatusCode httpStatusCode;
    private final HttpHeader responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode httpStatusCode, String responseBody, ContentType contentType) {
        this.httpStatusCode = httpStatusCode;
        this.responseHeader = buildInitialHeaders(responseBody, contentType);
        this.responseBody = responseBody;
    }

    public static HttpResponse ofStaticFile(String fileName, HttpStatusCode httpStatusCode, HttpCookie cookie) {
        if (!fileName.contains(".")) {
            fileName += ".html";
        }

        HttpResponse response = new HttpResponse(
                httpStatusCode,
                FILE_READER.read(fileName),
                ContentType.fromFileName(fileName)
        );

        if (!cookie.contains(JSESSIONID)) {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.add(JSESSIONID, UUID.randomUUID().toString());
            response.addHeader(SET_COOKIE, httpCookie.buildMessage());
        }

        return response;
    }

    private HttpHeader buildInitialHeaders(String responseBody, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_LENGTH.getName(), responseBody.getBytes().length + " ");
        headers.put(HttpHeaders.CONTENT_TYPE.getName(), contentType.getName() + ";charset=utf-8 ");
        return new HttpHeader(headers);
    }

    public void addHeader(String name, String value) {
        responseHeader.add(name, value);
    }

    public String buildMessage() {
        return String.join("\r\n",
                httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                "",
                responseBody
        );
    }
}
