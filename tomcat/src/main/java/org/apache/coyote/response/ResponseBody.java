package org.apache.coyote.response;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseBody {

    public static ResponseBody EMPTY = new ResponseBody(null);

    private final String source;

    public ResponseBody(final String source) {
        this.source = source;
    }

    public byte[] bytes() {
        return source.getBytes(UTF_8);
    }

    public int length() {
        return bytes().length;
    }

    public String source() {
        return source;
    }

    @Override
    public String toString() {
        return "ResponseBody.Length{" +
               "source='" + source.length() + '\'' +
               '}';
    }
}
