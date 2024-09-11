package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.exception.UnsupportedMethodException;

public abstract class Controller {
    protected abstract HttpResponse handle(HttpRequest request) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest request) throws IOException;

    protected abstract HttpResponse doGet(HttpRequest request) throws IOException;

    protected HttpResponse operate(HttpRequest request) throws IOException {
        HttpMethod method = request.getHttpMethod();
        if (method.isPost()) {
            return doPost(request);
        }
        if (method.isGet()) {
            return doGet(request);
        }
        throw new UnsupportedMethodException("Method is not supported: " + method);
    }
}
