package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;

public interface Handler {

    String message(Request request) throws IOException;

    String getMessage(Request request) throws IOException;

    String postMessage(Request request);
}
