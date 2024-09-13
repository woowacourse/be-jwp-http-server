package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String content;

    public RequestBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getFormData() {
        Map<String, String> formData = new HashMap<>();
        if (content == null || content.isEmpty()) {
            return formData;
        }

        String[] contentParts = content.split("&");
        for (String keyValuePair : contentParts) {
            String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length == 2) {
                formData.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return formData;
    }
}
