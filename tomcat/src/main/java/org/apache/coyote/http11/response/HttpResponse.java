package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpResponse {
    public static final String STATIC_PATH = "static";
    public static final String LINE_BREAK = "\n";
    private static final String DELIMITER = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_LINE = "";
    private static final String BLANK = " ";

    private String statusLine = "";
    private final List<HttpHeader> headers = new ArrayList<>();
    private String body = "";

    public void ok() {
        statusLine = HTTP_VERSION + BLANK + OK.getValue() + BLANK;
    }

    public void found() {
        statusLine = HTTP_VERSION + BLANK + FOUND.getValue() + BLANK;
    }

    public byte[] getBytes() {
        final String headerString = headers.stream()
                .map(HttpHeader::getHeaderAsString)
                .collect(Collectors.joining("\r\n"));
        return String.join(DELIMITER, statusLine, headerString, EMPTY_LINE, body).getBytes();
    }

    public String getStatusLine() {
        return statusLine;
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setContentType(final FileType fileType) {
        headers.add(HttpHeader.contentType(fileType));
    }

    public void setContentOfPlainText(final String plainText) {
        body = plainText;
        headers.add(HttpHeader.contentLengthOf(plainText));
    }

    public void setContentOfResources(final String filePath) throws IOException {
        final String responseBody = buildResponseBodyFromStaticFile(filePath);
        body = responseBody;
        headers.add(HttpHeader.contentLengthOf(responseBody));
    }

    private String buildResponseBodyFromStaticFile(final String filePath) throws IOException {
        final String resourceName = STATIC_PATH + filePath;
        final URL resourceURL = this.getClass().getClassLoader().getResource(resourceName);
        if (resourceURL == null) {
            throw new IllegalArgumentException("존재하지 않는 정적 리소스입니다.");
        }
        final Path path = Path.of(resourceURL.getPath());

        return String.join(LINE_BREAK, Files.readAllLines(path)) + LINE_BREAK;
    }

    public void addCookies(final HttpCookie cookie) {
        headers.addFirst(HttpHeader.setCookie(cookie));
    }

    public void sendRedirect(final String location) {
        headers.add(HttpHeader.location(location));
    }
}
