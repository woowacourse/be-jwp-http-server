package org.apache.coyote.request;

import java.util.Optional;

import org.apache.coyote.HttpMethod;

public class Request {

	private final RequestLine requestLine;
	private final RequestHeader header;
	private final RequestBody body;

	public Request(final RequestLine requestLine, final RequestHeader header, final RequestBody body) {
		this.requestLine = requestLine;
		this.header = header;
		this.body = body;
	}

	public boolean hasPath(final String path) {
		return requestLine.hasPath(path);
	}

	public boolean hasMethod(final HttpMethod method) {
		return requestLine.hasMethod(method);
	}

	public String findBodyField(final String key) {
		return body.findField(key);
	}

	public String findQueryParam(final String key) {
		return requestLine.findQueryParam(key);
	}

	public Optional<String> findCookie(final String key) {
		return header.findCookie(key);
	}

	public Optional<String> findSessionId() {
		return header.findSessionId();
	}

	public String getPath() {
		return requestLine.getPath();
	}
}
