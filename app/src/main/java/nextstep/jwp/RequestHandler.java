package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(inputStream);
            final HttpResponse httpResponse = new HttpResponse(outputStream);

            final String uri = httpRequest.getUri();
            Controller controller = RequestMapping.getController(uri);

            if (httpRequest.getCookies() != null) {
                HttpSession session = HttpSessions.getOrCreateSession(httpRequest.getCookies().get("JSESSIONID"));
                if (!HttpSessions.SESSIONS.containsKey(session.getId())) {
                    HttpSessions.SESSIONS.put(session.getId(), session);
                    httpResponse.setSession(session.getId());
                }
            }

            if (controller == null) {
                httpResponse.forward(getDefaultPath(uri));
                return;
            }
            controller.service(httpRequest, httpResponse);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getDefaultPath(String uri) {
        if ("/".equals(uri)) {
            return "/index.html";
        }
        return uri;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
