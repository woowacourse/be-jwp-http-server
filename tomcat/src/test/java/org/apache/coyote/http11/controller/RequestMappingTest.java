package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.techcourse.controller.HomePageController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestMappingTest {

    private final RequestMapping requestMapping = RequestMapping.getInstance();

    @DisplayName("/ 경로로 요청 시 homePageController를 반환한다.")
    @Test
    void getHomePageController() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(HomePageController.class);
        inputStream.close();
    }

    @DisplayName(".이 포함된 경로로 요청 시 staticResourceController를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/favicon.ico", "/css/styles.css"})
    void getStaticResourceController(String path) throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(StaticResourceController.class);
        inputStream.close();
    }

    @DisplayName("/login 경로로 요청 시 LoginController를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void getLoginController(String method) throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                method + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
        inputStream.close();
    }

    @DisplayName("/register 경로로 요청 시 RegisterController를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void getRegisterController(String method) throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                method + " /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
        inputStream.close();
    }
}
