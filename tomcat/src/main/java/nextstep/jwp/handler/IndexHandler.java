package nextstep.jwp.handler;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.HttpStatus;

public class IndexHandler {

    public static HttpResponse perform(HttpRequest request) {
        if (!request.getMethod().isGet()) {
            return HttpResponse.notFound();
        }

        String contentType = ContentType.HTML.getContentType();
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.OK)
                .header("Content-Type", contentType)
                .responseBody("Hello world!")
                .build();
    }
}
