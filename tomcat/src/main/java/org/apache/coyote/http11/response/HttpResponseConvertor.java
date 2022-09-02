package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;
import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.apache.coyote.http11.response.factory.ErrorResponseFactory;
import org.apache.coyote.http11.response.factory.StaticResponseFactory;
import org.apache.coyote.http11.utils.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseConvertor {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseConvertor.class);

    private HttpResponse response;


    public HttpResponseConvertor(HttpRequest request) {
        try {
            this.response = parseResponse(request);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            this.response = ErrorResponseFactory.NOT_FOUND.getValue();
        } catch (NoUserException e) {
            log.error(e.getMessage(), e);
            this.response = ErrorResponseFactory.UNAUTHORIZED.getValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.response = ErrorResponseFactory.INTERNAL_SERVER_ERROR.getValue();
        }
    }

    private HttpResponse parseResponse(HttpRequest request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getPath();

        if (method == GET && List.of("/", "").contains(path)) {
            return HttpResponse.from(new HttpResponseBody("Hello world!"), HttpStatus.OK, "text/html");
        }
        if (method == GET && path.split("\\?")[0].equals("/login")) {
            logIfLogin(path);
            return HttpResponse.from(HttpResponseBody.of(ROOT + "/login.html"), HttpStatus.OK, "text/html");
        }

        return StaticResponseFactory.getResponse(method, path);
    }

    private void logIfLogin(String uri) {
        if (!uri.contains("?")) {
            return;
        }
        User user = InMemoryUserRepository.findByAccount(new UriParser(uri).find("account"))
                .orElseThrow(NoUserException::new);
        log.info("userLogin: " + CRLF + user.toString() + CRLF);
    }

    public String getHeader() {
        return response.getHeader().getHeaders();
    }

    public String getResponse() {
        return String.join(CRLF, getHeader(), "", response.getBody().getBodyContext());
    }
}
