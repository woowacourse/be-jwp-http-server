package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.config.LoginFilter;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.filter.FilterChainManager;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.servlet.Servlet;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {

            final Request request = Request.from(inputStream);

            FilterChainManager filterChainManager = new FilterChainManager();
            filterChainManager.add(new LoginFilter());
            Response response = filterChainManager.getInitialChain().doFilter(request);

            if (!response.isFiltered()) {
                response = Servlet.getResponse(request);
            }

            outputStream.write(response.getResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
