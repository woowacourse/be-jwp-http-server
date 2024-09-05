package org.apache.coyote.http11.response;

import java.util.StringJoiner;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.HttpStatusCode;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader header;
    private final ResponseBody body;

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header) {
        this(statusCode, header, null);
    }

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader header, byte[] body) {
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, statusCode);
        this.header = header;
        this.body = new ResponseBody(body);
        header.setContentLength(String.valueOf(this.body.getBodyLength()));
    }

    public byte[] toByte() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");

        stringJoiner.add(statusLine.getReponseString());
        stringJoiner.add(header.toHeaderString());
        stringJoiner.add("\r\n");

        byte[] headerBytes = stringJoiner.toString().getBytes();
        if (!body.isEmpty()) {
            byte[] response = new byte[headerBytes.length + body.getBodyLength()];

            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(body.getBody(), 0, response, headerBytes.length, body.getBodyLength());

            return response;
        }
        return headerBytes;
    }
}
