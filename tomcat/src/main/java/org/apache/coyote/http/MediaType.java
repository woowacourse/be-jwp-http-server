package org.apache.coyote.http;

import java.util.Arrays;

public enum MediaType {

    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_X_WWW_FORM_URL_ENCODED("application/x-www-form-urlencoded", ""),
    ;

    public final String value;
    public final String fileExtension;

    MediaType(String value, String fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public static MediaType fromValue(String value) {
        return Arrays.stream(values())
                     .filter(mediaType -> mediaType.value.equals(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 타입입니다. 값: " + value));
    }

    public static MediaType fromFilePath(String filePath) {
        return Arrays.stream(values())
                     .filter(contentType -> filePath.endsWith(contentType.fileExtension))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 타입입니다. 경로: " + filePath));
    }
}
