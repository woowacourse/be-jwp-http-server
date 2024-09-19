package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.techcourse.model.User;

public final class SessionManager {
	private static final Map<Session, User> sessions = new ConcurrentHashMap<>();

	public static Session createSession(User user) {
		Session session = new Session();
		sessions.put(session, user);
		return session;
	}

	public static Optional<User> findUserBySession(Session session) {
		return Optional.ofNullable(sessions.get(session));
	}
}
