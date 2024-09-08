package org.apache.coyote.http11.response;

public class Http11Response {

    private static final String protocol = "HTTP/1.1";

    private final String responseBody;
    private final Http11ResponseHeaders headers;
    private final HttpStatusCode statusCode;
    private String firstLine = "";

    public Http11Response(HttpStatusCode httpStatusCode, String responseBody, Http11ResponseHeaders headers) {
        this.statusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.headers = headers;
    }

    public Http11Response(HttpStatusCode httpStatusCode, String responseBody, String fileExtensions) {
        this(httpStatusCode, responseBody,
                Http11ResponseHeaders.from(String.join("\r\n",
                "Content-Type: text/" + fileExtensions + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ")));
    }

    public byte[] getBytes() {
        setFirstLine();
        return String.join("\r\n",
                firstLine,
                headers.asString(),
                responseBody).getBytes();
    }

    private void setFirstLine() {
        firstLine = String.join(" ",
                protocol,
                String.valueOf(statusCode.getValue()),
                statusCode.getName()) + " ";
    }

    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }
}
