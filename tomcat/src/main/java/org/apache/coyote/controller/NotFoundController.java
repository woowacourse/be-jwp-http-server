package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStateCode;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.util.FileExtension;

public class NotFoundController implements Controller {

    @Override
    public HttpResponse run(HttpRequest request) {
        String path = "/404.html";
        String resourcePath = "static" + path;
        try {
            Path filePath = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
            MimeType mimeType = MimeType.from(FileExtension.from(path));
            byte[] body = Files.readAllBytes(filePath);
            return new HttpResponse(HttpStateCode.NOT_FOUND, mimeType, body);
        } catch (URISyntaxException | IOException e) {
            return new HttpResponse(HttpStateCode.INTERNAL_SERVER_ERROR, "Something Went Wrong".getBytes());
        }
    }
}
