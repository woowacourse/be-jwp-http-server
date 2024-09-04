package org.apache.coyote.http11.domain.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class ContentTypeResolver {

    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    private static final String CHARSET = ";charset=utf-8";
    private static final Set<String> TEXT_BASED_MIME_TYPES = Set.of(
            "text/plain",
            "text/html",
            "text/css",
            "application/json",
            "application/javascript",
            "application/xml",
            "text/xml"
    );

    public static String getContentType(String filePath) {
        Path path = Paths.get(filePath);
        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            return DEFAULT_MIME_TYPE;
        }

        if (contentType == null) {
            contentType = DEFAULT_MIME_TYPE;
        }

        if (isTextBased(contentType)) {
            contentType += CHARSET;
        }

        return contentType;
    }

    private static boolean isTextBased(String mimeType) {
        return TEXT_BASED_MIME_TYPES.contains(mimeType);
    }

}