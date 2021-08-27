package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParams("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(request.getParams("password"))) {
            log.info("User Login! account: {}", account);
        }
    }
}
