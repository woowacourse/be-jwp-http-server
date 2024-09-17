package org.apache.tomcat.http.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {

    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String PARAMETER_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    public Cookie(final String plaintext) {
        final var headerPair = List.of(plaintext.replaceAll(" ", "").split(HEADER_KEY_VALUE_DELIMITER, 2));

        values = Stream.of(headerPair.getLast().split(PARAMETER_DELIMITER))
                .map(param -> List.of(param.split(KEY_VALUE_DELIMITER)))
                .filter(paramList -> paramList.size() == 2)
                .collect(Collectors.toConcurrentMap(List::getFirst, List::getLast));
    }

    public String get(final String cookieName) {
        if (!values.containsKey(cookieName)) {
            throw new IllegalArgumentException("존재하지 않는 쿠키 값");
        }
        return values.get(cookieName);
    }
}
