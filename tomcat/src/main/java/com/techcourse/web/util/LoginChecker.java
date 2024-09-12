package com.techcourse.web.util;

import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.session.Session;
import org.apache.coyote.http11.http.session.SessionManager;

public class LoginChecker {

	public static boolean isLoggedIn(HttpRequest request) {
		HttpCookie cookie = request.getHeaders().getHttpCookie();
		if (cookie == null) {
			return false;
		}

		String sessionId = cookie.getJsessionid();
		if (sessionId == null || sessionId.isBlank()) {
			return false;
		}

		Session session = SessionManager.getSession(sessionId);
		return session != null && session.getUser() != null;
	}
}
