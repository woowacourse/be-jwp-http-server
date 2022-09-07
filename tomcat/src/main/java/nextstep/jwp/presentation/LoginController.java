package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.support.AbstractController;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.util.SessionManager;
import org.apache.coyote.util.CookieUtils;
import org.apache.coyote.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpHeaders headers = request.getHeaders();
        String cookie = headers.getValue("Cookie");
        if (cookie != null) {
            String[] split = cookie.split(";");
            for (String s : split) {
                String[] split1 = s.split("=");
                String session = split1[0];
                if (session.equals("JSESSIONID")) {
                    String sessionId = split1[1];
                    Session foundSession = SessionManager.findSession(sessionId);
                    if (foundSession != null) {
                        response.setStatus(HttpStatus.FOUND);
                        response.redirect("/index.html");
                        response.flush();
                    }
                }
            }
        }

        String body = FileUtils.readAllBytes(request.getPath().getValue());
        response.setStatus(HttpStatus.OK);
        response.setBody(body);
        response.flush();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        Map<String, String> values = getAccountAndPassword(request);
        String account = values.get("account");
        String password = values.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));

        if (!user.checkPassword(password)) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.redirect("/401.html");
            response.flush();
            return;
        }

        log.info(user.toString());
        String cookie = CookieUtils.ofJSessionId();
        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
        response.setStatus(HttpStatus.FOUND);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addLocation("/index.html ");
        httpHeaders.addCookie(HttpCookie.JSESSIONID + "=" + cookie);
        response.setHeaders(httpHeaders);
        response.flush();
    }

    private Map<String, String> getAccountAndPassword(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
    }
}
