package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HttpRequest(
        Method method,
        String path,
        Map<String, String> parameters,
        Map<String, String> headers,
        Map<String, String> cookies,
        ProtocolVersion protocolVersion,
        String body) {

    private static final String DELIMITER_COOKIE = "; ";
    private static final String DELIMITER_HEADER = ": ";
    private static final String DELIMITER_SPACE = " ";
    private static final String DELIMITER_VALUE = "=";
    private static final String DELIMITER_PARAMETER_KEYSET = "&";
    private static final String HEADER_NAME_COOKIE = "Cookie";

    public static HttpRequest parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(DELIMITER_SPACE);
        Method method = Method.from(startLineParts[0]);

        String path = "";
        Map<String, String> parameters = Map.of();
        Pattern pattern = Pattern.compile("([^?]+)(\\?(.*))?");
        Matcher matcher = pattern.matcher(startLineParts[1]);
        if (matcher.find()) {
            path = matcher.group(1);
            parameters = extractParameters(matcher.group(3));
        }

        ProtocolVersion protocolVersion = ProtocolVersion.from(startLineParts[2]);
        Map<String, String> headers = extractHeaders(lines);
        Map<String, String> cookies = extractCookies(headers.get(HEADER_NAME_COOKIE));

        String body = extractBody(lines);

        return new HttpRequest(method, path, parameters, headers, cookies, protocolVersion, body);
    }

    private static Map<String, String> extractParameters(String query) {
        Map<String, String> parameters = new HashMap<>();

        if (query != null) {
            String[] pairs = query.split(DELIMITER_PARAMETER_KEYSET);
            for (String pair : pairs) {
                String[] keyValue = pair.split(DELIMITER_VALUE);
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = URLDecoder.decode(keyValue[1], Charset.defaultCharset());
                    parameters.put(key, value);
                }
            }
        }

        return parameters;
    }

    private static Map<String, String> extractHeaders(List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.size() - 2; i++) {
            String[] lineParts = lines.get(i).trim().split(DELIMITER_HEADER);
            if (lineParts.length >= 2) {
                headers.put(lineParts[0], lineParts[1]);
            }
        }

        return headers;
    }

    private static Map<String, String> extractCookies(String cookieMessage) {
        if (cookieMessage == null) {
            return Map.of();
        }

        Map<String, String> cookies = new HashMap<>();

        for (String entry : cookieMessage.split(DELIMITER_COOKIE)) {
            int delimiterIndex = entry.indexOf(DELIMITER_VALUE);
            if (delimiterIndex == -1) {
                continue;
            }

            String key = entry.substring(0, delimiterIndex).trim();
            String value = entry.substring(delimiterIndex + 1).trim();
            cookies.put(key, value);
        }

        return cookies;
    }

    private static String extractBody(List<String> lines) {
        if (lines.size() > 1 && lines.get(lines.size() - 2).isEmpty()) {
            return lines.getLast();
        }
        return null;
    }

    public HttpRequest updatePath(String path) {
        return new HttpRequest(method, path, parameters, headers, cookies, protocolVersion, body);
    }

    public Map<String, String> extractUrlEncodedBody() {
        return extractParameters(body);
    }

    public boolean hasParameters(List<String> keys) {
        return keys.stream().allMatch(parameters::containsKey);
    }
}
