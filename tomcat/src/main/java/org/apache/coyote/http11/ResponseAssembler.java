package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class ResponseAssembler {

    private static final String RESOURCE_FOLDER = "static";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    public Http11Response resourceResponse(String url, HttpStatus httpStatus) {
        URL resource = getClass().getClassLoader()
                .getResource(RESOURCE_FOLDER + url);

        String extension = url.substring(url.lastIndexOf(".") + 1);
        String contentType = HttpContent.extensionToContentType(extension);
        long fileSize = new File(resource.getFile()).length();
        Map<String, String> headers = initHeader(contentType, fileSize);

        try {
            return new Http11Response(
                    httpStatus.getStatusCode(),
                    httpStatus.getMessage(),
                    headers,
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> initHeader(String contentType, long contentLength) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE.getHeaderName(), contentType + CHARSET_UTF_8);
        headers.put(HttpHeaders.CONTENT_LENGTH.getHeaderName(),
                Long.toString(contentLength));
        return headers;
    }

    public Http11Response rawStringResponse(String responseData) {
        Map<String, String> headers = initHeader(HttpContent.HTML.getContentType(),
                responseData.getBytes().length);

        return new Http11Response(
                HttpStatus.OK.getStatusCode(),
                HttpStatus.OK.getMessage(),
                headers,
                responseData
        );
    }
}
