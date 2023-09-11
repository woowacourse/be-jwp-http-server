package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestUri {

    private static final String URI_DELIMITER = "\\?";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String QUERY_ENTRY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String path;
    private final Map<String, String> queryString;


    private HttpRequestUri(final String path, final Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static HttpRequestUri from(final String uri) {
        if (!uri.contains(URI_DELIMITER)) {
            return new HttpRequestUri(uri, null);
        }
        return parseQueryString(uri);
    }

    private static HttpRequestUri parseQueryString(final String uri) {
        final String[] uriElements = uri.split(URI_DELIMITER);
        final String path = uriElements[KEY_INDEX];

        final Map<String, String> queryString = Pattern.compile(QUERY_ENTRY_DELIMITER)
                .splitAsStream(uriElements[VALUE_INDEX].trim())
                .map(queryEntry -> queryEntry.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]));

        return new HttpRequestUri(path, queryString);
    }

    public String getPath() {
        return path;
    }
}
