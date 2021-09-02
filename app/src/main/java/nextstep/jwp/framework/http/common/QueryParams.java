package nextstep.jwp.framework.http.common;

import static nextstep.jwp.framework.http.common.HttpHeaders.HEADER_DELIMITER;
import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams;

    public QueryParams() {
        this.queryParams = new HashMap<>();
    }

    public QueryParams(String query) {
        this.queryParams = createQueryParams(query);
    }

    private Map<String, String> createQueryParams(String query) {
        final Map<String, String> queryParams = new HashMap<>();
        final String[] params = query.split("&");

        for (String param : params) {
            final String[] element = param.split("=", 2);
            queryParams.put(element[KEY_INDEX].trim(), element[VALUE_INDEX].trim());
        }

        return queryParams;
    }

    public int count() {
        return queryParams.size();
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final String key : queryParams.keySet()) {
            builder.append(key)
                .append(HEADER_DELIMITER)
                .append(queryParams.get(key))
                .append(LINE_DELIMITER);
        }

        return builder.toString();
    }
}
