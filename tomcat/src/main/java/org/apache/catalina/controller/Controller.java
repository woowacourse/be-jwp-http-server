package org.apache.catalina.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
