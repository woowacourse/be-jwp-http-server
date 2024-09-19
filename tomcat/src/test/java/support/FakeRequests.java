package support;

import java.util.List;

public class FakeRequests {

    public static final String teLineRequest = String.join("\r\n",
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Connection: keep-alive ",
            "Connection: keep-alive ",
            "Connection: keep-alive ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!"
    );

    public static final List<String> validGetRequest = List.of(
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );
    public static final List<String> invalidGetRequest = List.of(
            "GETO/// HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );

    public static final List<String> invalidHeaderRequest = List.of(
            "GET / HTTP/1.1 ",
            "Host - localhost:8080 ",
            "Connection keep-alive "
    );
}
