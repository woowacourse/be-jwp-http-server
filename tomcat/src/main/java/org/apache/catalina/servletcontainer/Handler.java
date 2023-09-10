package org.apache.catalina.servletcontainer;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
