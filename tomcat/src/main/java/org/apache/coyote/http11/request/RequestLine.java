package org.apache.coyote.http11.request;

public class RequestLine {

    private final String method;
    private final String requestUrl;
    private final String protocol;

    public RequestLine(String method, String requestUrl, String protocol) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isPost() {
        return method.equals("POST");
    }

    public boolean isCss() {
        return requestUrl.contains(".css");
    }

    public boolean isJs() {
        return requestUrl.contains(".js");
    }

    public boolean isHtml() {
        return requestUrl.contains(".html");
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getProtocol() {
        return protocol;
    }
}
