package com.techcourse.handler;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import java.net.URI;

public class GetLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.getMethod().isGet();
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest, Manager sessionManager) {
        return new ForwardResult("login.html", HttpStatus.OK);
    }
}
