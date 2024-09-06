package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.Request;

public class Response {
    public static String writeResponse(Request request, String contentType, String content) {
        return String.join("\r\n",
                String.format("%s 200 OK ", request.getHttpVersion()),
                String.format("Content-Type: %s;charset=utf-8 ",contentType),
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }
}
