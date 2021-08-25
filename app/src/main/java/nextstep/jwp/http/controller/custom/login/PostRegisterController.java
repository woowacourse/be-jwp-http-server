package nextstep.jwp.http.controller.custom.login;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.common.ParameterExtractor;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.custom.CustomController;
import nextstep.jwp.http.exception.BadRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.Response;
import nextstep.jwp.http.response.response_line.ResponseLine;
import nextstep.jwp.model.User;

public class PostRegisterController extends CustomController {

    public static final String REGISTER_PATH = "/register";
    private static final String INDEX_PAGE_PATH = "/index.html";

    @Override
    public Response doService(HttpRequest httpRequest) {
        final var body = getBody(httpRequest);
        final var params = ParameterExtractor.extract(body);

        final String account = getUrlDecodedParam(params, "account");
        final String password = getUrlDecodedParam(params, "password");
        final String email = getUrlDecodedParam(params, "email");

        User user = new User(null, account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect(INDEX_PAGE_PATH);
    }

    private String getBody(HttpRequest httpRequest) {
        return httpRequest.getBody().getBody()
            .orElseThrow(BadRequestException::new);
    }

    private String getUrlDecodedParam(Map<String, String> params, String email) {
        try {
            return URLDecoder.decode(params.get(email), Charset.defaultCharset());
        } catch (NullPointerException e) {
            throw new BadRequestException();
        }
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    protected String path() {
        return REGISTER_PATH;
    }
}
