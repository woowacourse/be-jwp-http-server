package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestPath;

public class IndexHandler extends AbstractHandler {

    @Override
    public String getMessage(Request request) throws IOException {
        RequestPath requestPath = request.getRequestPath();
        final String responseBody = fileByPath(requestPath.path());
        return staticFileMessage(HTML, responseBody);
    }
}
