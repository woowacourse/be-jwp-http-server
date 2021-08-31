package nextstep.jwp.controller;

import nextstep.jwp.domain.request.HttpRequest;

public class RequestMapper {

    private RequestMapper() {
        throw new IllegalStateException("Utility Class");
    }

    public static Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getUri();
        if ("/".equals(path)) {
            return new DefaultController();
        }
        if ("/login".equals(path)) {
            return new LoginController();
        }
        if ("/register".equals(path)) {
            return new RegisterController();
        }
        return new IndexController();
    }
}
