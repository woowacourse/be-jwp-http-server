package com.techcourse.controller;

import com.techcourse.AuthManager;
import org.apache.catalina.ResourceManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import java.io.IOException;

import static com.techcourse.AuthManager.AUTHENTICATION_COOKIE_NAME;
import static org.apache.coyote.http11.Status.BAD_REQUEST;
import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.Status.OK;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Session session = AuthManager.register(httpRequest);

            httpResponse.setStatusLine(FOUND);
            httpResponse.setCookie(AUTHENTICATION_COOKIE_NAME, session.getId());
            httpResponse.setLocation("/index.html");
        } catch (IllegalArgumentException exception) {
            httpResponse.setStatusLine(BAD_REQUEST);
        }
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

            httpResponse.setStatusLine(OK);
            httpResponse.setContentType(httpRequest.getContentType());
            httpResponse.setResponseBody(responseBody);
            httpResponse.setContentLength(responseBody.getBytes().length);
        } catch (IOException exception) {
            httpResponse.setStatusLine(INTERNAL_SERVER_ERROR);
        }
    }
}
