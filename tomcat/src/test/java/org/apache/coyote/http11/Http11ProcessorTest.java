package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Set;

import org.apache.catalina.HandlerMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.jwp.handler.BaseRequestHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import nextstep.jwp.handler.StaticContentRequestHandler;
import support.StubSocket;

class Http11ProcessorTest {

	private HandlerMapping handlerMapping;

	@BeforeEach
	void setUp() {
		final var handlers = Set.of(
			new BaseRequestHandler(),
			new LoginRequestHandler(),
			new RegisterRequestHandler()
		);
		final var defaultHandler = new StaticContentRequestHandler();
		handlerMapping = new HandlerMapping(handlers, defaultHandler);
	}

	@Test
	void process() {
		// given
		final var socket = new StubSocket();
		final var processor = new Http11Processor(socket, handlerMapping);

		// when
		processor.process(socket);

		// then
		var expected = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: text/html;charset=utf-8 ",
			"Content-Length: 12 ",
			"",
			"Hello world!");

		assertThat(socket.output()).isEqualTo(expected);
	}

	@Test
	void index() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket, handlerMapping);

		// when
		processor.process(socket);

		// then
		final URL resource = getClass().getClassLoader().getResource("static/index.html");
		var expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 5564 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(socket.output()).isEqualTo(expected);
	}
}
