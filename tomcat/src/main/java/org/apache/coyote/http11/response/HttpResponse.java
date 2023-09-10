package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.IOException;
import java.util.UUID;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;

public class HttpResponse {

    private HttpRequest httpRequest;

    private HttpResponse(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public static HttpResponse empty() {
        return new HttpResponse(null);
    }

    public String getResponse() throws IOException {
        final var requestLine = httpRequest.requestLine();
        final var uri = requestLine.getUri();
        final var uuid = UUID.randomUUID();
        final var responseHeader = HttpResponseHeader.from(httpRequest);
        final var responseBody = HttpResponseBody.from(httpRequest);

        final StringBuilder body = new StringBuilder().append(parseHttpStatusLine(responseHeader)).append(CRLF)
                .append(parseContentTypeLine(uri)).append(CRLF)
                .append(parseContentLengthLine(responseBody)).append(CRLF);

        if (requestLine.isLoginSuccess() && httpRequest.cookie().noneJSessionId()) {
            body.append(parseCookieLine(uuid)).append(CRLF);
            body.append("Location: /index.html");
        }

        if (uri.startsWith("/login") && !httpRequest.cookie().noneJSessionId()) {
            body.append("Location: /index.html");
        }

        body.append(EMPTY).append(CRLF).append(responseBody.body());

        return body.toString();
    }

    private String parseCookieLine(final UUID uuid) {
        return String.format("Set-Cookie: JSESSIONID=%s", uuid);
    }

    private String parseHttpStatusLine(final HttpResponseHeader httpResponseHeader) {
        final HttpStatus httpStatus = httpResponseHeader.getHttpStatus();
        return String.join(
                SPACE,
                httpRequest.requestLine().getHttpVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.name(),
                ""
        );
    }

    private String parseContentTypeLine(final String uri) {
        if (uri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }

        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String parseContentLengthLine(final HttpResponseBody httpResponseBody) {
        return String.join(SPACE, "Content-Length:", String.valueOf(httpResponseBody.contentLength()), "");
    }

    public void setRequest(final HttpRequest request) {
        this.httpRequest = request;
    }

}
