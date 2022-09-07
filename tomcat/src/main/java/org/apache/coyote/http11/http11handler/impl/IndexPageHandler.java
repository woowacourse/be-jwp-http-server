package org.apache.coyote.http11.http11handler.impl;

import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.apache.coyote.http11.http11request.Http11Request;

public class IndexPageHandler implements Http11Handler {

    private static final String URI = "/index";
    private static final String URI_WITH_EXTENSION = "/index.html";

    private QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        String uri = queryStringProcessor.removeQueryString(http11Request.getUri());
        return uri.equals(URI) || uri.equals(URI_WITH_EXTENSION);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request, Visitor visitor) {
        return handlerSupporter.resourceResponseComponent(URI_WITH_EXTENSION, StatusCode.OK);
    }
}
