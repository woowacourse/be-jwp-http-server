package com.techcourse.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(final Exception e) {
        super(e);
    }
}
