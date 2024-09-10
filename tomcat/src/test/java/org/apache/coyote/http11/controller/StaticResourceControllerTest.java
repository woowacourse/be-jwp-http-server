package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.InputReader;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    @Test
    @DisplayName("빈 요청이 왔을 때 기본 파일이름을 통해 결과를 반환한다.")
    void process() throws IOException {
        Controller controller = StaticResourceController.getInstance();
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        InputReader inputReader = new InputReader(inputStream);
        HttpRequest httpRequest = new HttpRequest(inputReader);

        HttpResponse httpResponse = controller.process(httpRequest);

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getResponse()).isEqualTo(expected);
    }
}