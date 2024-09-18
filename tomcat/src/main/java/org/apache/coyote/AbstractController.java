package org.apache.coyote;

import org.apache.coyote.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        throw new UnsupportedHttpMethodException(request.getHttpMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedHttpMethodException(request.getHttpMethod());
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedHttpMethodException(request.getHttpMethod());
    }
}
