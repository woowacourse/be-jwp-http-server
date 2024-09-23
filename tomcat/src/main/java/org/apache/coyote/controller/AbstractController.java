package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {
    
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();

        validateMethod(httpMethod, response);

        if (httpMethod.isMethod(HttpMethod.POST)) {
            doPost(request, response);
        }

        if (httpMethod.isMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
    }

    private static void validateMethod(HttpMethod httpMethod, HttpResponse response) {
        if (!httpMethod.isValidMethod()) {
            response.setStatus(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
