package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService = new UserService();
    private final Http11Helper http11Helper = Http11Helper.getInstance();

    public String login(String request) throws IOException {
        String response = operate(request);

        return response;
    }

    @Override
    protected String doPost(String request) throws IOException {
        Map<String, String> requestBody = http11Helper.extractRequestBody(request);
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        if (account == null || password == null) {
            throw new UnauthorizedException("Values for authorization is missing.");
        }

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        String response = http11Helper.createResponse(HttpStatus.FOUND, "index.html");

        return response;
    }

    @Override
    protected String doGet(String request) throws IOException {
        String endpoint = http11Helper.extractEndpoint(request);
        String fileName = http11Helper.getFileName(endpoint);
        String response = http11Helper.createResponse(HttpStatus.OK, fileName);

        return response;
    }
}

