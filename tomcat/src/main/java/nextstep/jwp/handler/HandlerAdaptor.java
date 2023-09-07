package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.ExceptionHandler;
import nextstep.jwp.exception.HttpGlobalException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class HandlerAdaptor {

    private static final Map<String, RequestHandler> HANDLERS = new ConcurrentHashMap<>();

    static {
        HANDLERS.put("/", new HomeHandler());
        HANDLERS.put("/index", new IndexHandler());
        HANDLERS.put("/login", new LoginHandler());
        HANDLERS.put("/register", new RegisterHandler());
    }

    private HandlerAdaptor() {
    }

    public static HttpResponse handle(HttpRequest request) throws IOException {
        try {
            RequestHandler handler = extractHandler(request.getNativePath());

            return handler.handle(request);
        } catch (HttpGlobalException e) {
            return ExceptionHandler.handle(request, e.getHttpStatus());
        }
    }

    private static RequestHandler extractHandler(String nativePath) {
        return HANDLERS.keySet()
                .stream()
                .filter(nativePath::equals)
                .findAny()
                .map(HANDLERS::get)
                .orElseGet(ResourceHandler::getInstance);
    }
}
