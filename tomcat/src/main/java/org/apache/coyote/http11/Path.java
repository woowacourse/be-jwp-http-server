package org.apache.coyote.http11;

public class Path {

    private static final int EXCLUDE_SLASH_INDEX = 1;
    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;

    private final String value;

    public Path(String uri) {
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            this.value = uri.substring(0, queryParameterIndex);
            return;
        }
        this.value = uri;
    }

    public boolean isFileRequest() {
        return value.contains(".");
    }

    public String getFileName() {
        return value.substring(EXCLUDE_SLASH_INDEX);
    }

    public String extractContentType() {
        ContentType contentType = ContentType.findContentType(value);
        return contentType.name().toLowerCase();
    }

    public String value() {
        return value;
    }
}
