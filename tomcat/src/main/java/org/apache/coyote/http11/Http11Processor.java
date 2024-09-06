package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.resource.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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

            // 1. Request
            final var request = new HttpRequest(inputStream);

            // 1.1.1. Find Static Resource
            final var body = ResourceReader.readString(request.getPath());

            // 2. Response
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
