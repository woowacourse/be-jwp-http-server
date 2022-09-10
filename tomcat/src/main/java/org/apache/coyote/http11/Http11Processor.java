package org.apache.coyote.http11;

import org.apache.catalina.core.ServletContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestParser;
import org.apache.coyote.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer servletContainer;

    public Http11Processor(final Socket connection, final ServletContainer servletContainer) {
        this.connection = connection;
        this.servletContainer = servletContainer;
    }

    @Override
    public void run() {
        final Thread thread = Thread.currentThread();
        log.info("ThreadName: {}", thread.getName());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final Request request = RequestParser.parse(bufferedReader);
            final Response response = new Response(outputStream);

            servletContainer.findServlet(request.getUri())
                    .ifPresent(servlet -> servlet.service(request, response));

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
