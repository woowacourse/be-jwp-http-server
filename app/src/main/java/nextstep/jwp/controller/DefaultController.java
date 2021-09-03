package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.io.IOException;

public class DefaultController extends AbstractController {
    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.OK_200)
                .resource(httpRequest.getResource())
                .body("Hello world!")
                .build();
        response.forward();
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new IllegalArgumentException();
    }
}
