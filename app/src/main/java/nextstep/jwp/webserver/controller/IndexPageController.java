package nextstep.jwp.webserver.controller;

import java.io.IOException;
import java.util.EnumSet;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.util.Resources;

public class IndexPageController extends AbstractController {

    public IndexPageController() {
        super("/index", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        final String response = Resources.readString("static/index.html");

        return HttpResponse.ok()
                           .body(response)
                           .contentLength(response.getBytes().length)
                           .build();
    }
}
