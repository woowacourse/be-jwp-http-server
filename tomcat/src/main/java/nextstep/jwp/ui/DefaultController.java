package nextstep.jwp.ui;

import static org.apache.coyote.http11.response.StatusCode.OK;

import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.support.AbstractController;

public final class DefaultController extends AbstractController {

    @Override
    protected Http11Response doGet(final Http11Request request) {
        return Http11Response.withResponseBody(OK, "html", "Hello world!");
    }

    @Override
    protected Http11Response doPost(Http11Request request) {
        throw new MethodNotAllowedException();
    }
}
