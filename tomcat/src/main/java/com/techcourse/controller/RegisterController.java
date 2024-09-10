package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String registerHtml = StaticResourceProvider.getStaticResource("/register.html");
        response.setBody(registerHtml)
                .setContentType(MimeType.HTML.getMimeType());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        InMemoryUserRepository.save(new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        ));

        response.found("/index.html");
    }
}
