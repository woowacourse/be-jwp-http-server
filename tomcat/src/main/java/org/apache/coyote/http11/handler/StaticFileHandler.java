package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.apache.coyote.http11.common.HttpStatus.OK;
import static org.apache.coyote.http11.common.HttpStatus.REDIRECTION;

public class StaticFileHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final String EXTENSION = ".html";

    public static HttpResponse handle(final String requestURI, RequestHeader requestHeader) {
        try {
            String responseBody = findResponseBody(requestURI);
            if (requestURI.endsWith("html")) {
                return new HttpResponseBuilder().init()
                        .httpStatus(OK)
                        .header("Content-Type: text/html;charset=utf-8 ")
                        .header("Content-Length: " + responseBody.getBytes().length + " ")
                        .body(responseBody)
                        .build();
            }
            return new HttpResponseBuilder().init()
                    .httpStatus(OK)
                    .header("Content-Type: " + requestHeader.getHeaderValue("Accept").split(",")[0] + ";charset=utf-8 ")
                    .header("Content-Length: " + responseBody.getBytes().length + " ")
                    .body(responseBody)
                    .build();
        } catch (IOException | NullPointerException e) {
            log.error(e.getMessage(), e);
            return new HttpResponseBuilder().init()
                    .httpStatus(REDIRECTION)
                    .header("Location: /404.html ")
                    .build();
        }
    }

    private static String findResponseBody(final String requestURI) throws IOException {
        String requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestURI).getFile();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(requestedFile, Charset.forName("UTF-8")));
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str + "\n");
        }
        return sb.toString();
    }
}
