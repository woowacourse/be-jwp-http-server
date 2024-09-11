package com.techcourse.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.InvalidResourceException;
import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.FileExtension;
import com.techcourse.util.Resource;

public class FrontController extends Controller {
    private static final FrontController instance = new FrontController();
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Map<String, Controller> handlerMappings = new HashMap<>();

    public static FrontController getInstance() {
        return instance;
    }

    private FrontController() {
        initHandlerMappings();
    }

    private void initHandlerMappings() {
        handlerMappings.put("/login", LoginController.getInstance());
        handlerMappings.put("/register", RegisterController.getInstance());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String uri = request.getURI();
        Controller handler = getHandler(uri);
        String fileName = Resource.getFileName(uri);
        if (Objects.isNull(handler) && FileExtension.isFileExtension(fileName)) {
            try {
                HttpResponse response = getResourceResponse(fileName);
                return response;
            } catch (InvalidResourceException e) {
                log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());

                handler = NotFoundController.getInstance();
            }
        }
        if (Objects.isNull(handler)) {
            log.error("Error processing request for endpoint: {}", uri);

            handler = NotFoundController.getInstance();
        }
        try {
            HttpResponse response = handler.handle(request);
            return response;
        } catch (UnsupportedMethodException e) {
            log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());

            handler = MethodNotAllowedController.getInstance();
            HttpResponse response = handler.handle(request);
            return response;
        }
    }

    private HttpResponse getResourceResponse(String fileName) throws IOException {
        HttpResponse response = new HttpResponse();
        ResponseBody responseBody = new ResponseBody(Resource.read(fileName));
        response.setStatus(HttpStatus.OK);
        response.setContentType(MimeType.getMimeType(fileName));
        response.setBody(responseBody);
        return response;
    }

    private Controller getHandler(String uri) {
        return handlerMappings.get(uri);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: GET");
    }
}
