package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.common.header.EntityHeaders;
import org.apache.coyote.http11.common.header.GeneralHeaders;
import org.apache.coyote.http11.common.header.RequestHeaders;

public class Request {

    private final RequestLine requestLine;
    private final GeneralHeaders generalHeaders;
    private final RequestHeaders requestHeaders;
    private final EntityHeaders entityHeaders;
    private final String body;

    private Request(
            final RequestLine requestLine,
            final GeneralHeaders generalHeaders,
            final RequestHeaders requestHeaders,
            final EntityHeaders entityHeaders,
            final String body
    ) {

        this.requestLine = requestLine;
        this.generalHeaders = generalHeaders;
        this.requestHeaders = requestHeaders;
        this.entityHeaders = entityHeaders;
        this.body = body;
    }

    public static Request of(
            final String methodName,
            final String requestURI,
            final String protocolName,
            final Map<String, String> headers,
            final String body
    ) {

        return new Request(
                RequestLine.of(methodName, requestURI, protocolName),
                new GeneralHeaders(headers),
                new RequestHeaders(headers),
                new EntityHeaders(headers),
                body
        );
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri().toString();
    }

    public Cookies getCookies() {
        return Cookies.from(requestHeaders.getCookie());
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", generalHeaders=" + generalHeaders +
                ", requestHeaders=" + requestHeaders +
                ", entityHeaders=" + entityHeaders +
                ", body='" + body + '\'' +
                '}';
    }
}
