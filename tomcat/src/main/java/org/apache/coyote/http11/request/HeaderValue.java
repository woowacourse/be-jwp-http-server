package org.apache.coyote.http11.request;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderValue {

    private static final String MULTIPLE_VALUE_DELIMITER = ",";
    private static final String VALUE_OPTION_DELIMITER = ";";
    private static final String NO_OPTION = "";
    private static final int SINGLE_DELIMITER_COUNT = 1;

    private final List<String> values;
    private final String option;

    private HeaderValue(List<String> values, String option) {
        this.values = values;
        this.option = option;
    }

    public static HeaderValue empty() {
        return new HeaderValue(Collections.emptyList(), NO_OPTION);
    }

    public static HeaderValue from(String value) {
        if (hasSingleDelimiter(value)) {
            int optionIndex = value.indexOf(VALUE_OPTION_DELIMITER);
            List<String> values = separateValuesAndTrim(value.substring(0, optionIndex));
            String valueOption = value.substring(optionIndex + 1);
            return new HeaderValue(values, valueOption);
        }
        value = value.replace(VALUE_OPTION_DELIMITER, MULTIPLE_VALUE_DELIMITER);
        return new HeaderValue(separateValuesAndTrim(value), NO_OPTION);
    }

    private static boolean hasSingleDelimiter(String value) {
        return StringUtils.countMatches(value, VALUE_OPTION_DELIMITER) == SINGLE_DELIMITER_COUNT;
    }

    private static List<String> separateValuesAndTrim(String value) {
        List<String> values = Arrays.asList(value.split(MULTIPLE_VALUE_DELIMITER));
        return values.stream()
                     .map(String::trim)
                     .collect(Collectors.toList());
    }

    public List<String> getValues() {
        return values;
    }

    public String format() {
        String value = String.join(MULTIPLE_VALUE_DELIMITER, this.values);
        if (option.equals(NO_OPTION)) {
            return value;
        }
        return value + VALUE_OPTION_DELIMITER + option;
    }
}
