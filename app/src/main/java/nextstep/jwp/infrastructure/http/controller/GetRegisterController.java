package nextstep.jwp.infrastructure.http.controller;

import nextstep.jwp.infrastructure.http.View;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;

public class GetRegisterController implements Controller {

    @Override
    public HttpRequestLine requestLine() {
        return new HttpRequestLine(HttpMethod.GET, "/register");
    }

    @Override
    public View handle(final HttpRequest request) {
        return View.buildByResource("/register.html");
    }
}
