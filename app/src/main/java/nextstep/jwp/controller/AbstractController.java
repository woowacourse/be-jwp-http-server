package nextstep.jwp.controller;

import nextstep.jwp.exception.UnAuthorizedOperationException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

import java.io.IOException;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.isGet()) {
            doGet(httpRequest, httpResponse);
        }
        if (httpRequest.isPost()) {
            doPost(httpRequest, httpResponse);
        }
    }

    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new UnAuthorizedOperationException("사용할수 없는 메서드입니다.");
    }

    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new UnAuthorizedOperationException("사용할수 없는 메서드입니다.");
    }
}
