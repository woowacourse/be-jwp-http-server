package nextstep.jwp.ui;

import java.util.Optional;
import nextstep.jwp.application.AuthService;
import nextstep.jwp.application.dto.UserDto;
import nextstep.jwp.exception.LoginFailException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.request.mapping.controller.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScan;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HtmlResponse;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

@ControllerScan
public class LoginController implements Controller {

    private final AuthService authService = AuthService.getInstance();

    @Override
    @RequestMapping(method = HttpMethod.POST, uri = "/login")
    public HttpResponse handle(final HttpRequest httpRequest) {
        final RequestParams getRequestParams = httpRequest.getRequestParams();
        final Optional<String> account = getRequestParams.getValue("account");
        final Optional<String> password = getRequestParams.getValue("password");
        try {
            final String accountValue = account.orElseThrow(LoginFailException::new);
            final String passwordValue = password.orElseThrow(LoginFailException::new);
            return loginAndRedirectIndexPage(httpRequest, accountValue, passwordValue);
        } catch (final LoginFailException e) {
            return HtmlResponse.of(HttpStatus.UNAUTHORIZED, HttpHeaders.empty(), "401");
        }
    }

    private RedirectResponse loginAndRedirectIndexPage(final HttpRequest httpRequest,
                                                       final String account,
                                                       final String password) {
        final UserDto loginUser = authService.login(account, password);

        final RedirectResponse response = RedirectResponse.of("/index.html");
        if (!httpRequest.hasSession()) {
            setSessionUserId(loginUser.getId(), response);
        }
        return response;
    }

    private void setSessionUserId(final Long userId, final RedirectResponse response) {
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session();
        session.setAttribute("userId", userId);
        sessionManager.add(session);
        response.addSession(session.getId(), Session.DEFAULT_EXPIRED_MINUTES * 60);
    }
}
