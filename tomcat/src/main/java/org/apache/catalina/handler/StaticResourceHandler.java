package org.apache.catalina.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;

public class StaticResourceHandler {

    public static final String RESOURCE_BASE_PATH = "static";

    private static class LazyHolder {
        private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();
    }

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return LazyHolder.INSTANCE;
    }

    public boolean canHandleRequest(HttpRequest request) {
        String path = request.getPath();
        if (path.equals("/" )) {
            return false;
        }
        URL resource = findResource(request.getPath());
        return resource != null;
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        URL requestResource = findResource(request.getPath());
        if (requestResource == null || !checkGetMethod(request)) {
            URL notFoundUrl = findResource("/404.html" );
            String notFountContent = getResourceContent(notFoundUrl);
            response.setResponse(HttpStatusCode.NOT_FOUND, null, new ResponseBody(notFountContent));
            return;
        }

        String body = getResourceContent(requestResource);
        Map<String, String> header = createResponseHeader(request, body);
        response.setResponse(HttpStatusCode.OK, new ResponseHeader(header), new ResponseBody(body));
    }

    private boolean checkGetMethod(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET;
    }

    private URL findResource(String path) {
        return getClass().getClassLoader().getResource(RESOURCE_BASE_PATH + path);
    }

    private static String getResourceContent(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static Map<String, String> createResponseHeader(HttpRequest request, String body) {
        Map<String, String> header = new LinkedHashMap<>();

        String contentTypeValue = request.getContentType();
        if (contentTypeValue != null) {
            header.put(HttpHeaders.CONTENT_TYPE.getName(), contentTypeValue);
        } else {
            header.put(HttpHeaders.CONTENT_TYPE.getName(), HttpContentType.TEXT_HTML.getContentType());
        }

        header.put(HttpHeaders.CONTENT_LENGTH.getName(), String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        return header;
    }
}
