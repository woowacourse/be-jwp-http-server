package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.SafeExecutionWrapper;
import com.techcourse.model.User;
import java.util.List;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_SUCCESS_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public RegisterController() {
        List<Handler> handlers = List.of(
                Handler.ofPost(REGISTER_PATH, SafeExecutionWrapper.withExceptionHandling(this::doRegisterPost)),
                Handler.ofGet(REGISTER_PATH, SafeExecutionWrapper.withExceptionHandling(this::doRegisterGet))
        );
        registerHandlers(handlers);
    }

    private void doRegisterGet(HttpRequest request, HttpResponse response) {
        responseView(request, response);
    }

    private void doRegisterPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParameter(ACCOUNT);
        String password = request.getBodyParameter(PASSWORD);
        String email = request.getBodyParameter(EMAIL);
        User user = new User(account, password, email);

        validateRegisterAccount(account);
        InMemoryUserRepository.save(user);

        response.redirectTo(REGISTER_SUCCESS_PAGE);
    }

    private void validateRegisterAccount(String account) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    log.warn("이미 존재하는 사용자입니다: {}", user.getAccount());
                    throw new IllegalArgumentException("중복된 계정을 생성할 수 없습니다.");
                });
    }
}
