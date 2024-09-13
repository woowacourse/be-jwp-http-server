package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.HeaderKey.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.VersionOfProtocol;

public class HttpResponse {
	private ResponseLine responseLine;
	private Headers headers;
	private Body body;

	public HttpResponse() {
		this.responseLine = new ResponseLine();
		this.headers = new Headers();
	}

	public String toPlainText() {
		StringBuilder sb = new StringBuilder();
		sb.append(responseLine.generatePlainText());
		sb.append(headers.generatePlainText());
		sb.append("\r\n");
		if (body != null) {
			sb.append(body.value());
		}
		return sb.toString();
	}

	public void setRequestLine(String versionOfProtocol, HttpStatusCode httpStatusCode) {
		this.responseLine.setVersionOfProtocol(new VersionOfProtocol(versionOfProtocol));
		this.responseLine.setStatusCode(new StatusCode(httpStatusCode.getStatusCode()));
		this.responseLine.setStatusMessage(new StatusMessage(httpStatusCode.getStatusMessage()));
	}

	public void setStatusCode(int value) {
		this.responseLine.setStatusCode(new StatusCode(value));
	}

	public void setStatusMessage(String value) {
		this.responseLine.setStatusMessage(new StatusMessage(value));
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = new Headers(headers);
	}

	public void setBodyByPlainText(String body) {
		this.body = new Body(body);
	}

	public void setBodyByFileName(String filename) throws IOException {
		try {
			URL url = HttpResponse.class.getClassLoader().getResource(filename);
			File file = new File(url.getPath());
			this.body = new Body(new String(Files.readAllBytes(file.toPath())));

			ContentType contentType = ContentType.fromPath(url.getPath());
			this.headers.add(CONTENT_TYPE, contentType.getValue() + ";charset=utf-8");
			this.headers.add(CONTENT_LENGTH, String.valueOf(body.getContentLength()));
		} catch (AccessDeniedException | NullPointerException exception) {
			setStatusCode(404);
			setStatusMessage("NOT FOUND");
		}
	}

	public VersionOfProtocol getVersionOfProtocol() {
		return responseLine.getVersionOfProtocol();
	}

	public StatusCode getStatusCode() {
		return responseLine.getStatusCode();
	}

	public StatusMessage getStatusMessage() {
		return responseLine.getStatusMessage();
	}

	public Headers getHeaders() {
		return headers;
	}

	public Body getBody() {
		return body;
	}
}