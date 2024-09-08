package org.apache.coyote.http.response;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.MimeType;

public class ResponseHeader {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Header header;

    public ResponseHeader() {
        this(new Header());
    }

    private ResponseHeader(Header header) {
        this.header = header;
    }

    public void setContentType(String contentType) {
        header.addHeader(CONTENT_TYPE, contentType);
    }

    public void setContentLength(long contentLength) {
        header.addHeader(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        header.addHeader(LOCATION, location);
    }

    public void setCookie(String cookie) {
        header.addHeader(SET_COOKIE, cookie);
    }

    public String toResponse() {
        return header.toResponse();
    }

    public static ResponseHeader basicResponseHeader(int length) {
        Header header = new Header();
        header.addHeader(CONTENT_TYPE, MimeType.HTML.getContentType());
        header.addHeader(CONTENT_LENGTH, String.valueOf(length));
        return new ResponseHeader(header);
    }
}
