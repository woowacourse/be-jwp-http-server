package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;

public class DefaultHandler implements Handler {
    
    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpResponse initialResponse = HttpResponse.init(request.getHttpVersion());
        final HttpResponse afterSetHttpStatus = initialResponse.setHttpStatus(HttpStatus.OK);
        final HttpResponse afterSetContent = afterSetHttpStatus.setContent(request.getPath());
        return afterSetContent;
    }
}
