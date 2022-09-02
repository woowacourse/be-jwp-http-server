package org.apache.coyote.web;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.support.HttpMethod.GET;
import static org.apache.coyote.support.HttpMethod.POST;

import nextstep.jwp.controller.UserCreateController;
import nextstep.jwp.controller.UserLoginController;
import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public class RequestHandler {

    public Response handle(final Request request) {

        if (GET.isSameMethod(request.getMethod()) && request.isSameRequestUrl("/login")) {
            UserLoginRequest userLoginRequest = UserLoginRequest.from(request.getQueryParameters());
            return new UserLoginController().doGet(userLoginRequest);

        }

        if (POST.isSameMethod(request.getMethod()) && request.isSameRequestUrl("/register")) {
            UserRegisterRequest userRegisterRequest = UserRegisterRequest.from(request.parseBody());
            return new UserCreateController().doPost(userRegisterRequest);
        }

        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE.getValue(), ContentType.STRINGS.getValue())
        );
        return new BodyResponse(HttpStatus.OK, httpHeaders, "Hello world!");
    }
}
