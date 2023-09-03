package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;

public class LoginRequestHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

	private static final String REQUEST_PATH = "/login";
	private static final String LOGIN_PAGE_PATH = "/login.html";

	@Override
	public boolean canHandle(HttpRequest request) {
		return request.hasPath(REQUEST_PATH);
	}

	@Override
	public HttpResponse handle(final HttpRequest request) {
		if (request.hasMethod(HttpMethod.GET)) {
			return doGet(request);
		} else if (request.hasMethod(HttpMethod.POST)) {
			return doPost(request);
		}
		return HttpResponse.notFound();
	}

	private HttpResponse doGet(final HttpRequest request) {
		return HttpResponse.ok(ResourceProvider.provide(LOGIN_PAGE_PATH), MimeType.fromPath(LOGIN_PAGE_PATH));
	}

	private HttpResponse doPost(final HttpRequest request) {
		final var account = request.findBodyField("account");
		final var password = request.findBodyField("password");
		validateQueryParam(account, password);

		return login(account, password);
	}

	private void validateQueryParam(final String account, final String password) {
		if (account == null || password == null) {
			throw new IllegalArgumentException("필요한 정보가 없습니다.");
		}
	}

	private HttpResponse login(final String account, final String password) {
		final var optionalUser = InMemoryUserRepository.findByAccount(account);
		if (optionalUser.isEmpty()) {
			return HttpResponse.unauthorized();
		}
		final var user = optionalUser.get();
		if (!user.checkPassword(password)) {
			return HttpResponse.unauthorized();
		}

		log.info("[LOGIN SUCCESS] account: {}", account);
		return HttpResponse.redirect("/index.html");
	}
}
