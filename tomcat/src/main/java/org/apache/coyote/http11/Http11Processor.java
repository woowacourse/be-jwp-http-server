package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.catalina.mvc.Controller;
import org.apache.catalina.parser.HttpRequestParser;
import org.apache.catalina.parser.RequestMapping;
import org.apache.catalina.reader.RequestReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = readAndParserRequest(reader);
            HttpResponse response = findHttpResponse(request);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private HttpRequest readAndParserRequest(BufferedReader reader) {
        RequestReader requestReader = new RequestReader(reader);

        List<String> request = requestReader.readRequest();
        RequestLine requestLine = new RequestLine(request.getFirst());
        RequestHeader requestHeader = new RequestHeader(HttpRequestParser.parseHeaders(request));
        String body = requestReader.readBody(requestHeader.getContentLength());

        List<String> params = List.of(body.split(QUERY_PARAMETER_SEPARATOR));
        RequestBody requestBody = new RequestBody(HttpRequestParser.parseParamValues(params));
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private HttpResponse findHttpResponse(HttpRequest request) {
        HttpResponse response = HttpResponse.of(request);
        try {
            Controller controller = requestMapping.getController(request);
            controller.handleRequest(request, response);
        } catch (IllegalArgumentException e) {
            response.setError(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            response.setError(HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            response.setError(HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
