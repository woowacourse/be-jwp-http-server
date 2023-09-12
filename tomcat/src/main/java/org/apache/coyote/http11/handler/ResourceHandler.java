package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface ResourceHandler {

    boolean supports(final HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
