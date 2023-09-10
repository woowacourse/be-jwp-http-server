package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    boolean supports(final HttpRequest httpRequest);

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
