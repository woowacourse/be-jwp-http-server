package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.annotaion.GetMapping;
import org.apache.coyote.annotaion.PostMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.util.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/register")
    public void registerPage(HttpRequest request, HttpResponse response) {
        ViewResolver.resolveView("register.html", response);
    }

    @PostMapping("/register")
    public void save(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        User user = new User(formData.get("account"), formData.get("email"), formData.get("password"));
        InMemoryUserRepository.save(user);
        log.info("{} - 회원 가입 성공", user);
        response.sendRedirect("/index.html");
    }
}
