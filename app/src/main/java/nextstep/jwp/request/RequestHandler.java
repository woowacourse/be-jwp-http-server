package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (
            final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            RequestLine firstLine = new RequestLine(bufferedReader.readLine());
            RequestUri requestUri = new RequestUri(firstLine.split(" ")[1]);

            FileName fileName = requestUri.toFileName();
            RequestUrl requestUrl = new RequestUrl(getClass().getClassLoader(), fileName);
            RequestFile requestFile = requestUrl.toRequestFile();

            if (requestUri.isQueryMark()) {
                UserInfo userInfo = requestUri.getUserInfo();
                User user = new User(userInfo.getAccount(), userInfo.getPassword());
                InMemoryUserRepository.save(user);
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(firstLine)
                .append("\r\n");

            String line;
            while (!"".equals(line = bufferedReader.readLine())) {
                if (line == null) {
                    return;
                }
                stringBuilder.append(line)
                    .append("\r\n");
            }

            ResponseBody responseBody = new ResponseBody(Files.readAllBytes(requestFile.toPath()));
            final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getLength() + " ",
                "",
                responseBody.getBody());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
