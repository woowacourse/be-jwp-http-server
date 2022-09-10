package nextstep.jwp.presentation;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.support.AbstractController;

public class DefaultController extends AbstractController {

    private static final String BODY = "Hello world!";

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setBody(BODY);
        response.defaultForward();
        response.write();
    }
}
