package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.exception.InvalidRequestMethodException;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";

    private static final AbstractController RESOURCE_CONTROLLER = new ResourceController();

    public static AbstractController getInstance() {
        return RESOURCE_CONTROLLER;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        throw new InvalidRequestMethodException("지원하지 않는 메서드입니다.");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        URL url = getClass().getClassLoader().getResource(RESOURCE_BASE_PATH + request.getNativePath());

        if (url == null) {
            throw new NotFoundException("해당 URL을 찾을 수 없습니다. 요청 URL : " + request.getNativePath());
        }

        response.setContentType(ContentType.extractValueFromPath(request.getNativePath()));
        response.setBody(new String(Files.readAllBytes(Path.of(url.getPath()))));

    }
}
