package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.web.StaticResourceReader;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String responseBody =
            new StaticResourceReader(request.getUrl() + ContentType.HTML.getExtension()).content();

        response.setStatusLine(StatusCode.OK);
        response.addHeader("Content-Type", ContentType.HTML.getValue());
        response.addHeader("Content-Length", responseBody.getBytes().length + " ");
        response.addBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        loginService.login(request.getAttribute("account"), request.getAttribute("password"));

        response.setStatusLine(StatusCode.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
