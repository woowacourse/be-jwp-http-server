package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String path;
    private final String query;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public Http11Request(final HttpMethod httpMethod, final String path, final String query, final HttpHeader httpHeader, final RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean notContainJsessionId() {
        return httpHeader.notContainJsessionId();
    }

    public String findJsessionId() {
        return httpHeader.findJsessionId();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
