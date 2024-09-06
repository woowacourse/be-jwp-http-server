package com.techcourse.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface RequestHandler {

    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
