package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class RequestToResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestToResponse.class);

    private static final String STATIC = "static";

    public String build(HttpRequest request) throws IOException {
        Path path = request.getRequestLine().getPath();

        if (path.getPath().equals("/")) {
            return HttpResponse.basicResponse().toResponse();
        }

        if (path.getPath().startsWith("/login")) {
            return login(request.getRequestLine());
        }

        final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()));
        try {
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.getContentTypeFromExtension(path.getPath()));
            header.setContentLength(responseBody.getBytes().length);

            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        }
    }

    private String login(RequestLine requestLine) throws IOException {
        String uris = requestLine.getPath().getPath();
        if (!uris.contains("?")) {
            Path path = requestLine.getPath();
            final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()).concat(".html"));
            try {
                final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                StatusLine statusLine = new StatusLine(HttpStatus.OK);
                ResponseHeader header = new ResponseHeader();
                header.setContentType(MimeType.getContentTypeFromExtension(".html"));
                header.setContentLength(responseBody.getBytes().length);

                HttpResponse response = new HttpResponse(statusLine, header, responseBody);
                return response.toResponse();
            } catch (NullPointerException e) {
                return HttpResponse.notFoundResponses().toResponse();
            }
        }
        if (uris.contains("?")) {
            int index = uris.indexOf("?");
            String uri = uris.substring(0, index);
            String queryString = uris.substring(index + 1);
            final URL resource = getClass().getClassLoader().getResource("static" + uri + ".html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            List<String> infos = List.of(queryString.split("&"));
            List<String> ids = List.of(infos.get(0).split("="));
            List<String> passwords = List.of(infos.get(1).split("="));
            User user = InMemoryUserRepository.findByAccount(ids.get(1)).get();
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.HTML.getContentType());
            header.setContentLength(responseBody.getBytes().length);
            if (user.checkPassword(passwords.get(1))) {
                log.info(user.toString());
                header.setLocation("/index.html");
                HttpResponse response = new HttpResponse(
                        new StatusLine(HttpVersion.HTTP11, HttpStatus.FOUND),
                        header,
                        null
                );
                return response.toResponse();
            }
            header.setLocation("/401.html");
            HttpResponse response = new HttpResponse(
                    new StatusLine(HttpVersion.HTTP11, HttpStatus.FOUND),
                    header,
                    null
            );
            return response.toResponse();
        }
        return "";
    }
}
