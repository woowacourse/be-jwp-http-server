package nextstep.jwp.presentation;

import java.io.IOException;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.matchMethod("POST")) {
            return doPost(httpRequest, httpResponse);
        }
        if (httpRequest.matchMethod("GET")) {
            return doGet(httpRequest, httpResponse);
        }
        final HttpBody httpBody = HttpBody.createByUrl("/404.html");
        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.MOVED_TEMPORARILY)
                .contentType("/404.html")
                .contentLength(httpBody.getBody().getBytes().length)
                .location("/404.html");

        return new HttpResponse(httpHeader, httpBody);
    }

    protected HttpHeader defaultHeader(final StatusCode statusCode, final HttpBody httpBody, final String url) {
        return new HttpHeader().startLine(statusCode)
                .contentType(url)
                .contentLength(httpBody.getBody().getBytes().length);
    }

    protected abstract HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException;

    protected abstract HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse)
            throws IOException;
}
