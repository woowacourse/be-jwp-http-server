package org.apache.controller;

import java.util.Set;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class DefaultController extends AbstractController {

    private static final String URL = "/";
    private static final Set<HttpMethod> AVAILABLE_HTTP_METHODS = Set.of(HttpMethod.GET);

    public DefaultController() {
        super(URL, AVAILABLE_HTTP_METHODS);
    }

    @Override
    protected void doGet(Request request, Response response) {
        String body = "Hello world!";

        makeResourceResponse(request, response, HttpStatus.OK, body);
    }
}
