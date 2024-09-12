package org.apache.coyote.controller;

import org.apache.coyote.http11.message.common.FileExtension;
import org.apache.coyote.http11.message.common.HttpHeaderField;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.util.ResourceReader;

public class StaticResourceController extends AbstractController {

    private static final String STATIC_PREFIX = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = STATIC_PREFIX + request.getPath();
        String resource = ResourceReader.readResource(path);

        String extension = extractFileExtension(path);

        response.setStatusLine(HttpStatus.OK);
        response.setContentType(FileExtension.getFileExtension(extension).getContentType());
        response.setHeader(HttpHeaderField.CONTENT_LENGTH.getName(), String.valueOf(resource.getBytes().length));
        response.setBody(resource);
    }

    private String extractFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }
}
