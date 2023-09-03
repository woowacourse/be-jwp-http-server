package org.apache.coyote.http11.web;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private final HandlerMapping handlerMapping;

    public FrontController(final HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void handleHttpRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Controller controller = handlerMapping.findController(httpRequest.getHttpStartLine());
        final View view = controller.handleRequest(httpRequest, httpResponse);
        httpResponse.setHeader("Content-Type", view.getContentType());
        httpResponse.setBody(view.renderView());
    }
}
