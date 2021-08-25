package nextstep.jwp.framework.infrastructure.adapter.get;

import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class StaticRequestAdapter implements RequestAdapter {

    private final StaticFileResolver staticFileResolver;

    public StaticRequestAdapter(StaticFileResolver staticFileResolver) {
        this.staticFileResolver = staticFileResolver;
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        return staticFileResolver.render(httpRequest, HttpStatus.OK);
    }
}
