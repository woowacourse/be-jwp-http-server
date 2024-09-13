package com.techcourse.controller;

import static com.techcourse.controller.PagePath.INDEX_PAGE;
import static com.techcourse.controller.PagePath.REGISTER_PAGE;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.MissingRequestBodyException;
import com.techcourse.model.User;
import com.techcourse.model.exception.UserCreationException;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.was.controller.AbstractController;
import org.was.controller.ResponseResult;

public class RegisterController extends AbstractController {

    @Override
    protected ResponseResult doGet(HttpRequest request) {
        return ResponseResult
                .status(HttpStatusCode.OK)
                .path(REGISTER_PAGE.getPath());
    }

    @Override
    protected ResponseResult doPost(HttpRequest request) {
        Map<String, String> requestFormData = request.getFormData();
        if (requestFormData.isEmpty()) {
            return responseMissingBody();
        }

        try {
            registerUserByFormData(requestFormData);
        } catch (UserCreationException e) {
            return responseUserDataError(e.getMessage());
        }
        return responseRedirectIndex();
    }

    private void registerUserByFormData(Map<String, String> requestFormData) {
        String account = requestFormData.get("account");
        String password = requestFormData.get("password");
        String email = requestFormData.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
    }

    private ResponseResult responseMissingBody() {
        return ResponseResult
                .status(HttpStatusCode.BAD_REQUEST)
                .body(new MissingRequestBodyException().getMessage());
    }

    private ResponseResult responseUserDataError(String errorMessage) {
        return ResponseResult
                .status(HttpStatusCode.BAD_REQUEST)
                .body(errorMessage);
    }

    private static ResponseResult responseRedirectIndex() {
        return ResponseResult
                .status(HttpStatusCode.FOUND)
                .header(HttpHeaders.LOCATION.getName(), INDEX_PAGE.getPath())
                .build();
    }
}
