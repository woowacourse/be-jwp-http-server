package com.techcourse.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Controller {
    boolean canHandle(HttpRequest request);
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
