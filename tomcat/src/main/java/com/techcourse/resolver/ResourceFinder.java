package com.techcourse.resolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceFinder {

    String fileName;
    String extension;

    public ResourceFinder(String location) {
        String[] split = location.split("\\.");
        if (location.equals("/")) {
            location = "/index.html";
        } else if (split.length == 1) {
            location += ".html";
        } else if (split.length == 2) {
            location = location + "." + split[1];
        }
        this.fileName = location.split("\\.")[0];
        this.extension = location.split("\\.")[1];
    }

    public String getStaticResource(HttpResponse response) {
        URL url = getClass().getClassLoader().getResource("static" + fileName + "." + extension);
        response.addHeader("Content-Type",
                String.format("text/%s;charset=utf-8", extension.equals("js") ? "javascript" : extension));
        if (url == null) {
            url = getClass().getClassLoader().getResource("static" + "/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
        }
        try {
            return Files.readString(Path.of(url.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
