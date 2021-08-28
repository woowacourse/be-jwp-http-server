package nextstep.jwp.http;

public class HttpResponse {
    private static final String RESPONSE_FORMAT =
            String.join("\r\n",
                    "%s %s %s ",
                    "Content-Type: %s;charset=utf-8 ",
                    "Content-Length: %d ",
                    ""
            );

    private final String protocol;
    private final HttpStatus httpStatus;
    private final String contentType;
    private final Integer contentLength;
    private final String responseBody;

    public HttpResponse(final String protocol, final HttpStatus httpStatus, final String contentType, final Integer contentLength, final String responseBody) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.responseBody = responseBody;
    }

    //    final String response = String.join("\r\n",
//            "HTTP/1.1 200 OK ",
//            "Content-Type: text/html;charset=utf-8 ",
//            "Content-Length: " + responseBody.getBytes().length + " ",
//            "",
//            responseBody);

    public String toResponseMessage() {
        String header = String.format(RESPONSE_FORMAT,
                protocol,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                contentType,
                contentLength
        );

//        if (httpStatus.equals(HttpStatus.FOUND)) {
//            header += "Location: " + location + "\r\n";
//        }
        return header + "\r\n" + responseBody;
    }
}
