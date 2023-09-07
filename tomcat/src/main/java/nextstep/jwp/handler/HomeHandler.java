package nextstep.jwp.handler;

import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpStatusLine;
import nextstep.jwp.http.HttpVersion;

public class HomeHandler implements RequestHandler {

    public static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        HttpBody httpBody = HttpBody.from(DEFAULT_MESSAGE);
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
