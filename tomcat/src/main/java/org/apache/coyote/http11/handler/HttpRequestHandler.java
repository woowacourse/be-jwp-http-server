package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface HttpRequestHandler {

    HttpResponse handle(HttpRequest request) throws IOException;
}
