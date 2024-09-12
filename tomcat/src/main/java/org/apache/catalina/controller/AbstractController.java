package org.apache.catalina.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private final List<Handler> handlers = new ArrayList<>();

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Handler requestHandler = findHandler(request);
        requestHandler.handle(request, response);
    }

    private Handler findHandler(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("요청을 처리할 수 없는 컨트롤러입니다."));
    }

    public List<HttpEndpoint> getEndpoints() {
        return handlers.stream()
                .map(Handler::getEndpoint)
                .toList();
    }

    protected void registerHandlers(List<Handler> handlers) {
        this.handlers.addAll(handlers);
    }

    protected void responseView(HttpRequest request, HttpResponse response) {
        responseView(HttpStatus.OK, request, response);
    }

    protected void responseView(HttpStatus status, HttpRequest request, HttpResponse response) {
        String requestPath = request.getPath();
        File file = File.createHtml(requestPath);
        file.addToResponse(response);
        response.setHttpStatus(status);
    }
}
