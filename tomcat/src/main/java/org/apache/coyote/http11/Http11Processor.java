package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestMethodAndUrl = bufferedReader.readLine();

            log.info("GET 요청 = {}", requestMethodAndUrl);
            final String[] texts = requestMethodAndUrl.split(" ");
            final var path = texts[1];

            final Request request = new Request(path);
            log.info("request = {}", request);
            final URL resource = request.getUrl();
            final var result = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final var response = String.join("\r\n", "HTTP/1.1 200 OK ",
                    "Content-Type: " + request.getContentType() + ";charset=utf-8 ",
                    "Content-Length: " + result.getBytes().length + " ",
                    "",
                    result);

            if (request.getQueryString().containsKey("account")) {
                final var queryString = request.getQueryString();
                final String account = queryString.get("account");
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
                final String password = queryString.get("password");
                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }
                log.info("user = {}", user);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
