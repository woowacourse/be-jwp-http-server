package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/js"),
    ICO("ico", "image/apng"),
    DEFAULT("", "text/html");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType ofExtension(String extension) {
        return Arrays.stream(values())
            .filter(contentType -> extension.equals(contentType.extension))
            .findFirst()
            .orElse(ContentType.DEFAULT);
    }

    public String getType() {
        return type;
    }
}
