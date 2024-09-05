package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class Http11CookieParser {

    private static final String CRLF = "\r\n";

    private final Http11StartLineParser startLineParser;

    public Http11CookieParser(Http11StartLineParser startLineParser) {
        this.startLineParser = startLineParser;
    }

    public List<Http11Cookie> parseCookies(String requestMessage) {
        String rawHeaders = requestMessage.replace(startLineParser.parseStartLine(requestMessage), "")
                .replaceFirst(CRLF, "");

        return Arrays.stream(rawHeaders.split(CRLF))
                .filter(rawHeader -> rawHeader.startsWith("Cookie"))
                .map(this::removeHeaderKey)
                .flatMap(rawCookies -> {
                    String[] split = rawCookies.split(";");
                    return Arrays.stream(split);
                })
                .map(String::trim)
                .map(this::parseCookie)
                .toList();
    }

    private String removeHeaderKey(String rawHeader) {
        int startIndex = rawHeader.indexOf(":") + 1;
        String headerKeyRemoved = rawHeader.substring(startIndex);
        return headerKeyRemoved.trim();
    }

    private Http11Cookie parseCookie(String rawCookie) {
        String[] split = rawCookie.split("=");
        String key = split[0].trim();
        String value = split[1].trim();
        return new Http11Cookie(key, value);
    }
}
