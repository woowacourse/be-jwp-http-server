package org.apache.coyote.http11;

import com.techcourse.controller.ControllerMapping;
import com.techcourse.controller.dto.HttpResponseEntity;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestUri {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUri.class);

    private final PathInfo pathInfo;
    private final Map<String, String> queryParams;

    public HttpRequestUri(PathInfo pathInfo) {
        this(pathInfo, Collections.emptyMap());
    }

    public HttpRequestUri(PathInfo pathInfo, Map<String, String> queryParams) {
        this.pathInfo = pathInfo;
        this.queryParams = queryParams;
    }

    public HttpResponse<?> processParams(HttpMethod method) {
        if (queryParams.isEmpty()) {
            return new HttpResponseEntity<>().convertResponse();
        }
        ControllerMapping controllerMapping = pathInfo.getControllerMapping(method);
        HttpResponseEntity<?> response = controllerMapping.apply(queryParams);
        log.info("user : {}", response.body());
        return response.convertResponse();
    }

    public HttpResponse<?> processParams(HttpMethod method, Map<String, String> bodies) {
        ControllerMapping controllerMapping = pathInfo.getControllerMapping(method);
        HttpResponseEntity<?> response = controllerMapping.apply(bodies);
        return response.convertResponse();
    }

    public HttpResponse<String> getHttpResponse(HttpResponse<?> response) throws IOException {
        return pathInfo.getHttpResponse(response);
    }
}