package org.apache.coyote.response;

import java.util.Arrays;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    SVG("image/svg+xml"),
    ICO("image/x-icon"),
    ;

    private final String type;

    MimeType(String type) {
        this.type = type;
    }

    public static MimeType from(String fileName) {
        return Arrays.stream(values())
                .filter(mimeType -> fileName.endsWith(mimeType.name().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 확장자 입니다. fileName: %s"
                        .formatted(fileName)));
    }

    public String getType() {
        return this.type;
    }
}
