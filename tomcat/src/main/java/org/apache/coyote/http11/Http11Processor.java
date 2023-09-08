package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.DefaultHandler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.ResourceHandler;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestParser;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = new BufferedOutputStream(connection.getOutputStream())) {
            RequestParser requestParser = new RequestParser(inputStream);
            Request request = requestParser.parse();
            Response response = new Response();

            doHandler(request, response);

            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doHandler(Request request, Response response) {
        if (request.isSamePath("/")) {
            new DefaultHandler().response(request, response);
            return;
        }
        if (request.isSamePath("/login")) {
            new LoginHandler().login(request, response);
            return;
        }
        if (request.isSamePath("/register")) {
            new RegisterHandler().register(request, response);
            return;
        }
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.getResource(request, response);
    }
}
