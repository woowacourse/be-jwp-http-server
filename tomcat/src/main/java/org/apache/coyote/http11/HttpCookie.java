package org.apache.coyote.http11;

import java.util.Map;

public class HttpCookie {
    private static final String JSESSION_ID = "JSESSIONID";
    private static final String NO_JSESSION_ID = "";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getJsessionId() {
        return this.cookies.getOrDefault(JSESSION_ID, NO_JSESSION_ID);
    }
}
