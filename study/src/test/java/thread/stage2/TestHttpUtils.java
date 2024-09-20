package thread.stage2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHttpUtils {

    private static final Logger log = LoggerFactory.getLogger(SampleController.class);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(1))
            .build();

    public static HttpResponse<String> send(final String path) {
        log.info("connection requested");
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .timeout(Duration.ofSeconds(1))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            log.info("request sent");
            return response;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
