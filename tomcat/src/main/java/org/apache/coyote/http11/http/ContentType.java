package org.apache.coyote.http11.http;

public enum ContentType {
    TEXT_HTML_CHARSET_UTF_8("text/html;charset=utf-8"),
    TEXT_CSS_CHARSET_UTF_8("text/css;charset=utf-8"),
    TEXT_JS_CHARSET_UTF_8("text/js;charset=utf-8"),
    IMAGE_X_ICON("image/x-icon");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
