package nextstep.jwp.http.controller.custom.login;

import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.controller.custom.CustomController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;

public class GetLoginController extends CustomController {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE_RESOURCE_PATH = "/login.html";
    
    @Override
    public Response doService(HttpRequest httpRequest) {
        return HttpResponse.status(HttpStatus.OK, LOGIN_PAGE_RESOURCE_PATH);
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.GET;
    }

    @Override
    protected String path() {
        return LOGIN_PATH;
    }
}
