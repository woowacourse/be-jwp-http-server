package org.apache.coyote.http11.request.line;

import org.apache.coyote.http11.HttpProtocol;

public class RequestLine {

    private final Method method;
    private final Uri uri;
    private final HttpProtocol protocol;

    public RequestLine(final Method method, final Uri uri, final HttpProtocol protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static RequestLine from(String line) {
        String[] lineParts = line.split(" ");
        return new RequestLine(Method.from(lineParts[0]), new Uri(lineParts[1]), HttpProtocol.from(lineParts[2]));
    }

    public Method getMethod() {
        return method;
    }

    public Uri getUri() {
        return uri;
    }

    public String getUriPath() {
        return uri.getPath();
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getQueryParameter(String name) {
        return uri.getQueryParameter(name);
    }

    public boolean isMethod(Method target) {
        return method.equals(target);
    }

    public boolean isHttpProtocol(final HttpProtocol target) {
        return protocol.equals(target);
    }

    public boolean isUri(final Uri target) {
        return uri.equals(target);
    }

    public boolean isUriHome() {
        return uri.isHome();
    }

    public boolean isUriStartsWith(final Uri target) {
        return uri.isStartsWith(target);
    }
}
