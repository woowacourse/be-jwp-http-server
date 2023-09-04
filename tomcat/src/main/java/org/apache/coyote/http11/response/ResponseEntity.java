package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.IOException;
import java.util.UUID;
import org.apache.coyote.http11.auth.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.RequestURI;

public class ResponseEntity {

    private final RequestURI requestURI;

    private ResponseEntity(final RequestURI requestURI) {
        this.requestURI = requestURI;
    }

    public static ResponseEntity from(final RequestURI requestURI) {
        return new ResponseEntity(requestURI);
    }

    public String getResponse(final HttpCookie httpCookie) throws IOException {
        final var uri = requestURI.getUri();
        final var responseHeader = ResponseHeader.from(requestURI);
        final var responseBody = ResponseBody.from(requestURI);

        final StringBuilder body = new StringBuilder().append(parseHttpStatusLine(responseHeader)).append(CRLF)
                .append(parseContentTypeLine(uri)).append(CRLF)
                .append(parseContentLengthLine(responseBody)).append(CRLF);

        if (requestURI.isLoginSuccess() && httpCookie.noneJSessionId()) {
            body.append(parseCookieLine()).append(CRLF);
        }
        body.append(EMPTY).append(CRLF)
                .append(responseBody.body());
        return body.toString();
    }

    private String parseCookieLine() {
        return String.format("Set-Cookie: JSESSIONID=%s", UUID.randomUUID());
    }

    private String parseHttpStatusLine(final ResponseHeader responseHeader) {
        final HttpStatus httpStatus = responseHeader.getHttpStatus();
        return String.join(
                SPACE,
                requestURI.getHttpVersion(),
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

    private String parseContentLengthLine(final ResponseBody responseBody) {
        return String.join(SPACE, "Content-Length:", String.valueOf(responseBody.contentLength()), "");
    }

}
