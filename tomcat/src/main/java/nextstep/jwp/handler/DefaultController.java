package nextstep.jwp.handler;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController implements Controller {

    private static final Controller INSTANCE = new DefaultController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private DefaultController() {
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, HttpStatusCode.OK, "text/plain", "Hello world!");
    }
}
