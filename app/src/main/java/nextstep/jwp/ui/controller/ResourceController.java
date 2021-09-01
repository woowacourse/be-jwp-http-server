package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public class ResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        response.forward(path);
    }
}
