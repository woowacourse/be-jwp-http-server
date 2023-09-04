package org.apache.coyote.http11.handler;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static org.apache.coyote.header.ContentType.CHARSET_UTF_8;
import static org.apache.coyote.header.ContentType.TEXT_CSS;
import static org.apache.coyote.header.ContentType.TEXT_HTML;
import static org.apache.coyote.header.HttpMethod.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHttp11MethodHandler implements Http11MethodHandler {

    private static final Logger log = LoggerFactory.getLogger(GetHttp11MethodHandler.class);

    @Override
    public HttpMethod supportMethod() {
        return GET;
    }

    @Override
    public String handle(final String request) {
        String targetPath = RequestExtractor.extractTargetPath(request);
        if (targetPath.equals("/")) {
            return defaultContent();
        }

        if (targetPath.contains("?")) {
            Map<String, String> queryParams = RequestExtractor.extractQueryParam(request);
            log.info("user : {}", findByAccount(queryParams.get("account")).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 유저입니다.")
            ));

            targetPath = targetPath.substring(0, targetPath.indexOf("?"));
        }

        if (!targetPath.contains(".")) {
            targetPath += ".html";
        }
        return resourceContent(targetPath);
    }

    private String defaultContent() {
        final var responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String resourceContent(final String targetPath) {
        URL resourcePath = getClass().getClassLoader().getResource("static" + targetPath);

        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(new File(resourcePath.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType(targetPath) + ";" + CHARSET_UTF_8 + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String contentType(final String targetPath) {
        if (targetPath.contains(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
