package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.Response.redirect;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.QueryParameters;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response.ServletResponse;

public class UserController implements Controller {

    private UserController() {
    }

    public static ServletResponse login(final Request request) {
        final var form = QueryParameters.from(request.getBody());
        final var account = form.findSingleByKey("account");
        final var password = form.findSingleByKey("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> redirectLoginUser(request, user))
                .orElseGet(() -> redirect("/401.html"));
    }

    private static ServletResponse redirectLoginUser(final Request request, final User user) {
        final var session = request.getSession();
        session.setAttribute("user", user);

        return redirect("/index.html")
                .addSetCookie(Cookies.ofJSessionId(session.getId()));
    }

    public static ServletResponse register(final Request request) {
        final var form = QueryParameters.from(request.getBody());
        final var account = form.findSingleByKey("account");
        final var email = form.findSingleByKey("email");
        final var password = form.findSingleByKey("password");

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return redirect("/index.html");
    }

}
