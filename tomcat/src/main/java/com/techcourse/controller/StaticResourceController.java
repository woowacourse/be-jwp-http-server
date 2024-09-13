package com.techcourse.controller;

import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;

public class StaticResourceController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        //todo 예외 처리?
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getTarget().equals("/")) {
            StaticResource staticResource = new StaticResource("/index.html");
            response.setResponseOfStaticResource(staticResource);
            return;
        }
        StaticResource staticResource = new StaticResource(request.getTarget());
        response.setResponseOfStaticResource(staticResource);
    }
}
