package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MimeType;

public class SignupRequestHandler extends AbstractRequestHandler {

    private static final String REDIRECTION_PATH = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = ResourceReader.readFile(httpRequest.getRequestURI());
        httpResponse.ok(MimeType.HTML, body);
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        User newUser = new User(param.get(ACCOUNT_KEY), param.get(PASSWORD_KEY), param.get(EMAIL_KEY));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        Session session = httpRequest.getSession();
        session.setUserAttribute(newUser);
        SessionManager.getInstance().add(session);

        httpResponse.setSession(session);
        httpResponse.found(REDIRECTION_PATH);
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.existsUser(user)) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }
}
