package org.apache.catalina;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public abstract class RequestHandler {

	private final String requestPath;

	protected RequestHandler(final String requestPath) {
		this.requestPath = requestPath;
	}

	public Response handle(final Request request) {
		if (request.hasMethod(HttpMethod.GET)) {
			return doGet(request);
		} else if (request.hasMethod(HttpMethod.POST)) {
			return doPost(request);
		}
		return Response.notFound();
	}

	protected Response doGet(final Request request) {
		return Response.notFound();
	}

	protected Response doPost(final Request request) {
		return Response.notFound();
	}

	public String getRequestPath() {
		return requestPath;
	}
}
