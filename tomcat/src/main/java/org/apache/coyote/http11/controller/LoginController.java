package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends Controller {

    @Override
    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isExistQueryString()) {
            return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/index");
        }

        return HttpResponse.createWithBody(HttpStatus.OK, httpRequest.getRequestUri());
    }
}
