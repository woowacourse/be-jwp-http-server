package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body = new HashMap<>();

    public HttpRequestBody(String bodyLine) {
        for (String parameter : bodyLine.split("&")) {
            int index = parameter.indexOf("=");
            if (index == -1) {
                break;
            }
            String key = parameter.substring(0, index).trim();
            String value = parameter.substring(index + 1).trim();
            body.put(key, value);
        }
    }

    public String get(String key) {
        return body.get(key);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
