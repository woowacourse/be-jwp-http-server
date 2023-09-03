package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;

public class ResourceHandler implements RequestHandler{
    private static final RequestHandler handler = new ResourceHandler();

    private ResourceHandler(){}

    public static RequestHandler getInstance() {
        return handler;
    }
    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        URL url = getClass().getClassLoader().getResource("static"+ request.getNativePath());
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = createHeaders(httpBody, request);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private HttpHeaders createHeaders(HttpBody httpBody, HttpRequest request) {
        List<String> headers = List.of(
                "Content-Type: " + ContentType.extractValueFromPath(request.getNativePath()) + ";charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength()
        );

        return HttpHeaders.from(headers);
    }
}
