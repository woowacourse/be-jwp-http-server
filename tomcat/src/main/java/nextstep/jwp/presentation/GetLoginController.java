package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class GetLoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(GetLoginController.class);
    private static final String SESSION_ID = "JSESSIONID";

    @Override
    public String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException {
        Map<String, String> cookies = httpRequestParser.findCookies();
        if (cookies.containsKey(SESSION_ID) && SessionManager.isAlreadyLogin(cookies.get(SESSION_ID))) {
            return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, "/index.html");
        }
        return httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, "/login.html");
    }
}
