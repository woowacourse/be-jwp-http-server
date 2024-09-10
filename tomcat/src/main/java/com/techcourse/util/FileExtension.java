package com.techcourse.util;

public enum FileExtension {
    HTML("html"),
    CSS("css"),
    JS("js"),
    SVG("svg"),
    PNG("png"),
    JPG("jpeg"),
    ;

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public static boolean isFileExtension(String path) {
        for (FileExtension extension : values()) {
            if (path.endsWith(extension.getExtension())) {
                return true;
            }
        }
        return false;
    }

    public String getExtension() {
        return extension;
    }
}