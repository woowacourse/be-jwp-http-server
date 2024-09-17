package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatusCode;

public class ClientRequestException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String errorPagePath;

    public ClientRequestException(HttpStatusCode statusCode, String errorPagePath) {
        this.statusCode = statusCode;
        this.errorPagePath = errorPagePath;
    }

    public void handleErrorResponse() {
        ErrorResponseHandler.getInstance()
                .handleErrorMessage(statusCode, errorPagePath);
    }
}
