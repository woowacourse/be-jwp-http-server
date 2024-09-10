package org.apache.catalina.http.startline;

public class HttpResponseLine {

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public HttpResponseLine(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.httpStatus = HttpStatus.OK;
    }

    public boolean isValid() {
        return httpVersion != null && httpStatus != null;
    }

    public String stringify() {
        return httpVersion.getValue() + " " + httpStatus.getValue();
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
