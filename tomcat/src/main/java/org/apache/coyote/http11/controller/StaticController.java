package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;

public class StaticController implements Controller {

    private static final String INDEX_URI = "/";

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getUri().equals(INDEX_URI)) {
            return new HttpResponse(StatusCode.OK, ContentType.TEXT_HTML.getValue(), "Hello world!");
        }

        return new HttpResponse(StatusCode.OK, "text/" + request.getExtension(),
                ViewLoader.from(request.getUri()));
    }
}
