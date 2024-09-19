package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.resource.ResourceParser;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class RegisterControllerTest {

    @Test
    void 요청_URI가_register이고_GET_메서드라면_회원가입_페이지로_이동한다() throws IOException {
        final var result = TestHttpUtils.sendGet("http://127.0.0.1:8080", "/register");

        File registerPage = ResourceParser.getRequestFile("/register.html");
        String expectBody = new String(Files.readAllBytes(registerPage.toPath()));

        assertThat(result.body()).isEqualTo(expectBody);
        assertThat(result.statusCode()).isEqualTo(200);
    }

    @Test
    void 요청_URI가_register이고_POST_메서드라면_회원가입을_수행후_메인페이지로_리다이렉트한다() {
        String body = "account=newUser&password=password&email=test@test.com";

        // when
        final var result = TestHttpUtils.sendPost("http://127.0.0.1:8080", "/register", body);

        // then
        assertThat(result.statusCode()).isEqualTo(302);
        assertThat(result.headers().map().containsKey("Location")).isTrue();
        assertThat(result.headers().map().get("Location")).isEqualTo(List.of("/index.html"));
    }
}
