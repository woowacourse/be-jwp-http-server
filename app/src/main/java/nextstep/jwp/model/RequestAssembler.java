package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAssembler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAssembler.class);

    private final InputStream inputStream;

    public RequestAssembler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Request assemble() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        LOGGER.debug(line);
        String[] firstLine = line.split(" ");
        String requestMethod = firstLine[0];
        RequestPath requestPath = new RequestPath(firstLine[1]);
        Map<String, String> headers = headers(reader, line);
        RequestBody requestBody = requestBody(reader, headers);
        return new Request(requestMethod, headers, requestPath, requestBody);
    }

    private RequestBody requestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }
        LOGGER.debug(body);
        return new RequestBody(body);
    }

    private Map<String, String> headers(BufferedReader reader, String line) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (!"".equals(line)) {
            if (line == null) {
                return headers;
            }
            line = reader.readLine();
            LOGGER.debug(line);
            String[] parsedLine = line.split(": ");
            if (parsedLine.length < 2) {
                break;
            }
            headers.put(parsedLine[0], parsedLine[1]);
        }
        return headers;
    }
}
