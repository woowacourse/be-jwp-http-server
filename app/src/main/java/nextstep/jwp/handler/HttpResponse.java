package nextstep.jwp.handler;

import java.util.HashMap;

import nextstep.jwp.util.ContentType;
import nextstep.jwp.util.File;

public class HttpResponse {
    public static final String LOCATION_HEADER = "Location";

    private final String httpVersion;
    private final HttpHeader httpHeader;
    
    private HttpStatus httpStatus;
    private String body;

    public HttpResponse(HttpRequest httpRequest) {
        this.httpHeader = new HttpHeader(new HashMap<>());
        this.httpVersion = httpRequest.getHttpVersion();
    }

    public void ok(File file) {
        httpStatus = (HttpStatus.OK);
        body(file.getContent(), file.getContentType());
    }

    public void redirect(String url, File file) {
        httpStatus = (HttpStatus.FOUND);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    public void unauthorized(String url, File file) {
        httpStatus = (HttpStatus.UNAUTHORIZED);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    public void notFound(String url, File file) {
        httpStatus = (HttpStatus.NOT_FOUND);
        addHttpHeader(LOCATION_HEADER, url);
        body(file.getContent(), file.getContentType());
    }

    private void body(String body, ContentType contentType) {
        this.httpHeader.addHeader("Content-Type", contentType.getValue());
        this.httpHeader.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }
    public void addHttpHeader(String key, String value) {
        this.httpHeader.addHeader(key, value);
    }

    public String makeHttpMessage() {
        return String.join("\r\n",
                httpVersion + " " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                httpHeader.makeHttpMessage(),
                "",
                body);
    }
}
