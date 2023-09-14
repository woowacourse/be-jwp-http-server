package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("application/javascript", ".js"),
    ICO("image/x-icon", ".ico"),
    SVG("image/svg+xml", ".svg"),
    ;

    private final String type;
    private final String fileExtension;

    ContentType(final String type, final String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public static String getByPath(final String path) {
        return Arrays.stream(values())
                .filter(it -> path.endsWith(it.fileExtension))
                .map(it -> it.type)
                .findAny()
                .orElse(HTML.type);
    }

    public static boolean matchesFileExtension(final String path) {
        return Arrays.stream(values())
                .anyMatch(contentType -> path.endsWith(contentType.fileExtension));
    }
}
