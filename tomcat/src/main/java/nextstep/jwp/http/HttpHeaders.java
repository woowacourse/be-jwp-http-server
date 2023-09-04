package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> httpHeaders;

    private HttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static HttpHeaders from(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new HttpHeaders(headers);
    }

    public static HttpHeaders createDefaultHeaders(String requestNativePath, HttpBody httpBody) {
        Map<String, String> headers = new LinkedHashMap<>();

        headers.put("Content-Type", ContentType.extractValueFromPath(requestNativePath));
        headers.put("Content-Length", String.valueOf(httpBody.getBytesLength()));

        return new HttpHeaders(headers);
    }

    public void setLocation(String value) {
        httpHeaders.put("Location", value);
    }

    public void addHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public boolean containsKey(String key) {
        return httpHeaders.containsKey(key);
    }

    public String getHeaders() {
        return httpHeaders.keySet()
                .stream()
                .map(key -> key + ": " + httpHeaders.get(key) + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public String get(String key) {
        if (containsKey(key)) {
            return httpHeaders.get(key);
        }

        return null;
    }

}
