package nextstep.jwp.server.handler.controller.standard;

import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.request.request_line.HttpPath;
import nextstep.jwp.http.message.response.HttpResponse;

public class GetStandardController implements StandardController {

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        HttpPath path = httpRequest.getPath();
        return HttpResponse.status(HttpStatus.OK, path.getUri());
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.GET);
    }
}
