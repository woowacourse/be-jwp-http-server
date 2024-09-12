package org.apache.coyote.http11.response;

import java.util.Set;

public class MimeTypes {
    public static final Set<String> textFiles = Set.of(
            "text/plain",
            "text/html",
            "text/css",
            "text/javascript",
            "application/javascript",
            "text/xml",
            "text/markdown"
    );

    public static String getMimeTypes(String contentType) {
        if (textFiles.contains(contentType)) {
            return contentType + ";charset=utf-8";
        }
        return contentType;
    }
}