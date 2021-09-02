package nextstep.jwp.http.cookie;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class HttpCookie {

    public static final HttpCookie EMPTY = new HttpCookie(Collections.emptyMap());

    private static final String KEY_SESSION_ID = "JSESSIONID";

    private final Map<String, String> params;

    private HttpCookie(Map<String, String> params) {
        this.params = params;
    }

    public static HttpCookie of(String cookieLine) {
        if (Objects.isNull(cookieLine) || cookieLine.isEmpty()) {
            return EMPTY;
        }
        Map<String, String> items = Arrays.stream(cookieLine.split("; "))
                .map(line -> line.split("="))
                .filter(pair -> pair.length == 2)
                .collect(toMap(pair -> pair[0], pair -> pair[1]));
        return new HttpCookie(items);
    }

    public String getAttributes(String name) {
        return params.get(name);
    }

    public String getSessionId() {
        return params.get(KEY_SESSION_ID);
    }

    public boolean hasSessionId() {
        return params.containsKey(KEY_SESSION_ID);
    }

    public HttpSession getSession(){
        if (hasSessionId() && HttpSessions.contains(getSessionId())) {
            return HttpSessions.getSession(getSessionId());
        }
        return HttpSession.create();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
