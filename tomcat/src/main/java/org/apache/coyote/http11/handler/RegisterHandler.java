package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.Method;
import org.apache.coyote.model.request.RequestBody;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.Util;

import static org.apache.coyote.utils.Util.createResponse;

public class RegisterHandler implements Handler {

    public static final String REGISTER_HTML = "/register.html";
    public static final String INDEX_HTML = "/index.html";
    private static final String CLIENT_ERROR_404 = "/404.html";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    private final HttpRequest httpRequest;

    public RegisterHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.checkMethod(Method.GET)) {
            return createResponse(StatusCode.OK, Util.getResponseBody(REGISTER_HTML, getClass()))
                    .getResponse();
        }
        if (httpRequest.checkMethod(Method.POST)) {
            saveUser();
            return createResponse(StatusCode.FOUND, Util.getResponseBody(INDEX_HTML, getClass()))
                    .getResponse();
        }
        return createResponse(StatusCode.UNAUTHORIZED, Util.getResponseBody(CLIENT_ERROR_404, getClass()))
                .getResponse();
    }

    private void saveUser() {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getByKey(ACCOUNT);
        String password = requestBody.getByKey(PASSWORD);
        String email = requestBody.getByKey(EMAIL);
        System.out.println(account);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
