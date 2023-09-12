package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class ControllerMapperTest {

    private static final ControllerMapper controllerMapper = new ControllerMapper(
            Map.of("/login", new LoginController(), "/register", new RegisterController())
    );

    @Test
    void POST_로그인_요청시_LoginController를_반환한다() {
        final HttpRequest request = HttpRequest.of("POST /login HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller controller = controllerMapper.findController(request);

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void GET_로그인_요청시_LoginController를_반환한다() {
        final HttpRequest request = HttpRequest.of("GET /login HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller controller = controllerMapper.findController(request);

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void POST_회원가입_요청시_RegisterController를_반환한다() {
        final HttpRequest request = HttpRequest.of("POST /register HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller controller = controllerMapper.findController(request);

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void GET_회원가입_요청시_RegisterController를_반환한다() {
        final HttpRequest request = HttpRequest.of("GET /register HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller controller = controllerMapper.findController(request);

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void 요청을_처리할_컨트롤러가_없으면_null을_반환한다() {
        final HttpRequest request = HttpRequest.of("GET /index.html HTTP/1.1 ", new LinkedHashMap<>(), "");
        final Controller controller = controllerMapper.findController(request);

        assertThat(controller).isNull();
    }
}
