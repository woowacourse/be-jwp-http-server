package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public HttpResponse process(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return HttpResponse.ok("/login.html", FileReader.read("/login.html"));
        }
        try {
            final String account = httpRequest.getHttpBody("account");
            final String password = httpRequest.getHttpBody("password");
            checkUser(account, password);
            final HttpResponse httpResponse = HttpResponse.found("/index.html", FileReader.read("/index.html"));
            httpResponse.setCookie("JSESSIONID", HttpCookie.createJSessionId());
            return httpResponse;
        } catch (RuntimeException e) {
            return HttpResponse.unauthorized("/401.html", FileReader.read("/401.html"));
        }
    }

    private void checkUser(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new UncheckedServletException("비밀번호가 일치하지 않습니다.");
        }
        log.info(user.toString());
    }
}
