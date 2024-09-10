package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusLine;

public class UnauthorizedController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            String fileName = "static/401.html";
            var resourceUrl = getClass().getClassLoader().getResource(fileName);
            Path filePath = Path.of(resourceUrl.toURI());
            String responseBody = new String(Files.readAllBytes(filePath));

            return HttpResponse.unauthorized(httpRequest)
                    .contentType(Files.probeContentType(filePath) + ";charset=utf-8")
                    .contentLength(String.valueOf(responseBody.getBytes().length))
                    .responseBody(responseBody)
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
