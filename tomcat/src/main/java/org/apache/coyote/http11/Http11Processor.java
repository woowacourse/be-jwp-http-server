package org.apache.coyote.http11;

import com.techcourse.controller.ControllerAdviser;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int BUFFER_SIZE = 1024;

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
        HttpRequest request = null;
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            request = createRequest(inputStream);
            log.info("[REQUEST] = {}", request);
            HttpResponse response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (UncheckedServletException e) {
            ControllerAdviser.service(e, request, new HttpResponse());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createRequest(InputStream inputStream) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)){
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[BUFFER_SIZE];
            int readByteCount;

            while ((readByteCount = bufferedInputStream.read(bytes)) != -1) {
                String requestString = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);
                sb.append(requestString);
            }

            return HttpRequest.create(sb.toString());
        } catch (IOException e) {
            log.error("IO Exception occur during make request object");
        }
        return null;
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        FrontController frontController = FrontController.getInstance();
        frontController.service(request, response);
        return response;
    }
}
