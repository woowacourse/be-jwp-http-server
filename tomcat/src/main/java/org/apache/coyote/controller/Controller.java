package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request) throws Exception;
}
