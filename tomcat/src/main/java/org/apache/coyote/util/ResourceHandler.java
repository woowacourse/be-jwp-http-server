package org.apache.coyote.util;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;

public class ResourceHandler {

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = httpRequest.getRequestURL();
        try {
            httpResponse.setResponseBody(FileReader.read(path));
            httpResponse.setMimeType(MimeType.from(path));
            httpResponse.setStatus(HttpStatus.OK);
        } catch (IOException | URISyntaxException | NullPointerException exception) {
            httpResponse.setEmptyResponseBody();
            httpResponse.setMimeType(MimeType.NONE);
            httpResponse.setStatus(HttpStatus.NOT_FOUND);
        }
    }
}
