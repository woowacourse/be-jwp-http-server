package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    
    private HttpStatusLine httpStatusLine;
    private HttpResponseHeader httpResponseHeader;
    private String httpResponseBody;

    public HttpResponse() {
        this.httpStatusLine = new HttpStatusLine("HTTP/1.1", HttpStatus.OK);
        this.httpResponseHeader = new HttpResponseHeader();
        this.httpResponseBody = "";
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatusLine.setHttpStatus(httpStatus);
    }

    public void addHttpResponseHeader(String key, String value) {
        this.httpResponseHeader.add(key, value);
    }

    public void setJSessionId(String jSessionId) {
        this.httpResponseHeader.setJSessionId(jSessionId);
    }

    public void setContentType(HttpRequest httpRequest) {
        if (httpRequest.matchesFileExtension(".css")) {
            this.httpResponseHeader.add("Content-Type", "text/css");
            return;
        }
        if (httpRequest.matchesFileExtension(".js")) {
            this.httpResponseHeader.add("Content-Type", "application/javascript");
            return;
        }
        this.httpResponseHeader.add("Content-Type", "text/html;charset=utf-8");
    }

    public void setHttpResponseBody(String resourceName) throws IOException {
        if (resourceName.equals("Hello world!")) {
            this.httpResponseBody = resourceName;
            return;
        }
        this.httpResponseBody = getResponseBody(resourceName);
    }

    private String getResponseBody(String resourceName) throws IOException {
        if (!resourceName.endsWith(".html") && resourceName.lastIndexOf(".") == -1) {
            resourceName += ".html";
        }

        URL resource = getClass().getClassLoader().getResource("static" + resourceName);
        Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    public String toHttpResponse() {
        StringBuilder httpResponse = new StringBuilder();

        String responseStatusLine = String.format(
                "%s %d %s ",
                this.httpStatusLine.getHttpVersion(),
                this.httpStatusLine.getHttpStatusCode(),
                this.httpStatusLine.getHttpStatusMessage()
        );
        httpResponse.append(responseStatusLine).append(CRLF);

        appendHeader(httpResponse, "Set-Cookie", this.httpResponseHeader.getSetCookieValue());
        appendHeader(httpResponse, "Content-Type", this.httpResponseHeader.get("Content-Type"));
        appendHeader(httpResponse, "Content-Length", String.valueOf(this.httpResponseBody.getBytes().length));
        appendHeader(httpResponse, "Location", this.httpResponseHeader.get("Location"));

        httpResponse.append(CRLF);
        httpResponse.append(this.httpResponseBody);
        return httpResponse.toString();
    }

    private void appendHeader(StringBuilder httpResponse, String headerName, String headerValue) {
        if (headerValue != null && !headerValue.isEmpty()) {
            httpResponse.append(String.format("%s: %s ", headerName, headerValue)).append(CRLF);
        }
    }
}
