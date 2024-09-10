package org.apache.coyote.http11.response;

import java.util.Objects;

public class ResponseBody {
    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public int getLength() {
        if (Objects.isNull(body) || body.isEmpty()) {
            return 0;
        }
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }
}
