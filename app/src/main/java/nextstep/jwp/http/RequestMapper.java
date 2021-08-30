package nextstep.jwp.http;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestMapper {

    public static AbstractController map(HttpRequest httpRequest) {
        if (httpRequest.uri().equals("/") || httpRequest.uri().equals("/index.html")) {
            return new HomeController(httpRequest);
        }
        if (httpRequest.uri().startsWith("/login")) {
            return new LoginController(httpRequest);
        }
        if (httpRequest.uri().startsWith("/register")) {
            return new RegisterController(httpRequest);
        }
        return new Controller(httpRequest);
    }
}
