package org.apache.tomcat.http.common.body;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BodyMapper {

    EMPTY("", TextTypeBody::new),
    TEXT_PLAIN("text/plain", TextTypeBody::new),
    TEXT_HTML("text/html", TextTypeBody::new),
    TEXT_CSS("text/css", TextTypeBody::new),
    TEXT_JS("text/javascript", TextTypeBody::new),
    FORM_URL_ENCODED("application/x-www-form-urlencoded", FormUrlEncodeBody::new);

    private static final Map<String, BodyMapper> CONVERTER = Arrays.stream(BodyMapper.values())
            .collect(Collectors.toConcurrentMap(BodyMapper::getMimeType, Function.identity()));


    private final String mimeType;
    private final Function<String, Body> mapping;

    BodyMapper(final String mimeType, final Function<String, Body> mapping) {
        this.mimeType = mimeType;
        this.mapping = mapping;
    }

    public static Function<String, Body> getMapping(final String plaintext) {
        return CONVERTER.get(plaintext).mapping;
    }

    public String getMimeType() {
        return mimeType;
    }
}
